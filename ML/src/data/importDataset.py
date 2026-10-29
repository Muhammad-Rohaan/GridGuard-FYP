import pandas as pd
import psycopg2
from psycopg2.extras import execute_values
from datetime import datetime

# ====================== CONFIG ======================
DB_CONFIG = {
    "host": "localhost",
    "port": 5432,
    "dbname": "GridGuard",
    "user": "postgres",
    "password": "0000"          # <-- change this
}

CSV_FILE = "data/Electric PowerLoad Data_CSVs/Residential/5_min_Residential.csv"                # your CSV file path
AREA_CODE = "AREA-R1"                   # the single residential area
# ====================================================

def main():
    print("Connecting to PostgreSQL...")
    conn = psycopg2.connect(**DB_CONFIG)
    cur = conn.cursor()

    # 1. Get the area_id of AREA-R1
    cur.execute("SELECT id FROM areas WHERE code = %s", (AREA_CODE,))
    result = cur.fetchone()

    if not result:
        print(f"ERROR: Area with code '{AREA_CODE}' not found!")
        print("Please create the area first.")
        return

    area_id = result[0]
    print(f"Found Area ID: {area_id}")

    # 2. Read CSV
    print("Reading CSV...")
    df = pd.read_csv(CSV_FILE)

    # Clean column names
    df.columns = [c.strip() for c in df.columns]

    # Rename columns if needed
    if "Time" in df.columns and "Power (kW)" in df.columns:
        df = df.rename(columns={"Time": "recorded_at", "Power (kW)": "load_kw"})
    elif "timestamp" in df.columns:
        df = df.rename(columns={"timestamp": "recorded_at", "power": "load_kw"})

    # Convert time
    df["recorded_at"] = pd.to_datetime(df["recorded_at"])

    print(f"Total rows in CSV: {len(df)}")

    # 3. Optional: Clear old data for this area
    print("Deleting old readings for this area...")
    cur.execute("DELETE FROM load_readings WHERE area_id = %s", (area_id,))
    conn.commit()

    # 4. Prepare data for bulk insert
    data = [
        (area_id, row.recorded_at.to_pydatetime(), float(row.load_kw))
        for row in df.itertuples()
    ]

    # 5. Bulk insert (very fast)
    print("Inserting data into load_readings...")
    insert_query = """
        INSERT INTO load_readings (area_id, recorded_at, load_kw)
        VALUES %s
        ON CONFLICT (area_id, recorded_at) DO NOTHING
    """

    execute_values(cur, insert_query, data, page_size=5000)
    conn.commit()

    # 6. Verify
    cur.execute("SELECT COUNT(*) FROM load_readings WHERE area_id = %s", (area_id,))
    count = cur.fetchone()[0]
    print(f"Successfully imported {count} rows!")

    cur.close()
    conn.close()
    print("Done.")

if __name__ == "__main__":
    main()