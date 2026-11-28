import gzip
import os
import re
import shutil
import sqlite3

SRC_DB = "/workspaces/qaf_app/dehkhoda/dehkhoda.sqlite"
DST_DB = "/workspaces/qaf_app/dehkhoda/dehkhoda_opt.sqlite"
DST_GZ = "/workspaces/qaf_app/dehkhoda/dehkhoda_opt.sqlite.gz"

# پاکسازی شواهد شعری و متنی در دهخدا بدون آسیب به تعریف اصلی
CITATION_SPLIT_RE = re.compile(r"\s*:\s*")
MULTISPACE_RE = re.compile(r"\s+")


def clean_definition(text: str) -> str:
    if not text:
        return ""
    text = text.strip()

    # اگر علامت «:» وجود دارد، بررسی می‌کنیم آیا بخش بعد از آن شاهد شعری/متنی است یا خیر
    match = CITATION_SPLIT_RE.search(text)
    if match:
        before = text[: match.start()].strip()
        after = text[match.end() :].strip()

        # شرط ایمنی: قبل از «:» باید حداقل ۶ کاراکتر تعریف واقعی باشد
        # و عبارت قبل از آن با «به معنی» یا «رجوع به» تمام نشده باشد
        is_prefix_def = before.endswith(("معنی", "معنای", "رجوع به", "یعنی", "عبارت از"))
        if len(before) >= 6 and not is_prefix_def and len(after) >= 35:
            text = before

    # پاکسازی فاصله‌های اضافی و نیم‌فاصله‌های تکراری
    text = MULTISPACE_RE.sub(" ", text).strip()
    # حذف نقطه یا ویرگول سرگردان در انتها در صورت نیاز نیست، اما فاصله قبل از نقطه را اصلاح می‌کنیم
    text = text.replace(" .", ".").replace(" ،", "،")
    return text


def main():
    if os.path.exists(DST_DB):
        os.remove(DST_DB)
    if os.path.exists(DST_GZ):
        os.remove(DST_GZ)

    src = sqlite3.connect(SRC_DB)
    dst = sqlite3.connect(DST_DB)

    dst.execute("PRAGMA page_size = 4096;")
    dst.execute("PRAGMA journal_mode = MEMORY;")
    dst.execute("PRAGMA synchronous = OFF;")
    dst.execute("PRAGMA temp_store = MEMORY;")

    # ساخت اسکیمای سبک‌شده
    dst.executescript(
        """
        CREATE TABLE entries (
            id INTEGER PRIMARY KEY,
            headword TEXT,
            headword_plain TEXT NOT NULL,
            pronunciation TEXT,
            is_proper_noun INTEGER NOT NULL DEFAULT 0
        );

        CREATE TABLE senses (
            entry_id INTEGER NOT NULL,
            sense_order INTEGER NOT NULL,
            pos TEXT,
            definition TEXT NOT NULL,
            PRIMARY KEY (entry_id, sense_order)
        ) WITHOUT ROWID;
        """
    )

    print("1/4 در حال کپی و پاکسازی جدول entries...")
    # فقط مدخل‌هایی که حداقل یک معنی دارند نگه داشته می‌شوند
    entries_cur = src.execute(
        """
        SELECT e.id, e.headword, e.headword_plain, e.pronunciation, COALESCE(e.is_proper_noun, 0)
        FROM entries e
        WHERE EXISTS (SELECT 1 FROM senses s WHERE s.entry_id = e.id)
        """
    )

    entries_batch = []
    entry_count = 0
    for row in entries_cur:
        eid, hw, hwp, pron, is_pn = row
        # اگر headword با headword_plain یکی است، NULL ذخیره کن تا فضا ذخیره شود
        hw_opt = None if hw == hwp else hw
        pron_opt = pron.strip() if pron and pron.strip() else None
        entries_batch.append((eid, hw_opt, hwp, pron_opt, is_pn))
        entry_count += 1
        if len(entries_batch) >= 10000:
            dst.executemany(
                "INSERT INTO entries (id, headword, headword_plain, pronunciation, is_proper_noun) VALUES (?, ?, ?, ?, ?)",
                entries_batch,
            )
            entries_batch.clear()
    if entries_batch:
        dst.executemany(
            "INSERT INTO entries (id, headword, headword_plain, pronunciation, is_proper_noun) VALUES (?, ?, ?, ?, ?)",
            entries_batch,
        )
    dst.commit()
    print(f"   -> {entry_count:,} مدخل ثبت شد.")

    print("2/4 در حال کپی و حذف شواهد شعری در جدول senses...")
    senses_cur = src.execute(
        "SELECT entry_id, sense_order, pos, definition FROM senses"
    )

    senses_batch = []
    sense_count = 0
    samples = []

    for eid, sorder, pos, defn in senses_cur:
        cleaned = clean_definition(defn)
        if not cleaned:
            continue
        pos_opt = pos.strip() if pos and pos.strip() else None

        # ذخیره چند نمونه که تغییر کرده‌اند برای بررسی چشمی
        if len(samples) < 8 and defn and len(defn) - len(cleaned) > 80:
            samples.append((eid, defn[:140], cleaned[:140]))

        senses_batch.append((eid, sorder, pos_opt, cleaned))
        sense_count += 1
        if len(senses_batch) >= 10000:
            dst.executemany(
                "INSERT INTO senses (entry_id, sense_order, pos, definition) VALUES (?, ?, ?, ?)",
                senses_batch,
            )
            senses_batch.clear()
    if senses_batch:
        dst.executemany(
            "INSERT INTO senses (entry_id, sense_order, pos, definition) VALUES (?, ?, ?, ?)",
            senses_batch,
        )
    dst.commit()
    print(f"   -> {sense_count:,} معنی ثبت شد.")

    print("3/4 در حال ساخت ایندکس و اجرای VACUUM...")
    dst.execute("CREATE INDEX idx_entries_headword_plain ON entries(headword_plain);")
    dst.commit()
    dst.execute("VACUUM;")
    dst.close()
    src.close()

    print("4/4 در حال فشرده‌سازی با Gzip (حداکثر فشرده‌سازی - سطح 9)...")
    with open(DST_DB, "rb") as f_in, gzip.open(DST_GZ, "wb", compresslevel=9) as f_out:
        shutil.copyfileobj(f_in, f_out)

    src_mb = os.path.getsize(SRC_DB) / (1024 * 1024)
    dst_mb = os.path.getsize(DST_DB) / (1024 * 1024)
    gz_mb = os.path.getsize(DST_GZ) / (1024 * 1024)

    print("\n================ گزارش نتیجه ================")
    print(f"حجم دیتابیس اصلی:          {src_mb:.2f} MB")
    print(f"حجم دیتابیس بهینه‌شده:     {dst_mb:.2f} MB  (کاهش {((src_mb - dst_mb)/src_mb)*100:.1f}%)")
    print(f"حجم فایل فشرده (.gz):      {gz_mb:.2f} MB")
    print("=============================================\n")

    print("--- نمونه‌های پاکسازی‌شده (برای بررسی دقت) ---")
    for eid, before_snip, after_snip in samples:
        print(f"[ID {eid}]")
        print(f"  قبل: {before_snip}...")
        print(f"  بعد: {after_snip}")
        print()


if __name__ == "__main__":
    main()
