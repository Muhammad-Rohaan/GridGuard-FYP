import pandas as pd
import matplotlib.pyplot as plt

# 1. Load data
filepath = '2017_1hour_Residential.csv'
df = pd.read_csv(filepath)

# 2. Check column data types and clean up spaces
df.columns = df.columns.str.strip()

# 3. Convert 'Power (kW)' to numeric values (coercing non-numeric strings to NaN if any)
df['Power (kW)'] = pd.to_numeric(df['Power (kW)'], errors='coerce')

# 4. Convert 'Time' to datetime format for proper X-axis plotting
df['Time'] = pd.to_datetime(df['Time'])

# 5. Drop rows where Power (kW) is missing/NaN
df_clean = df.dropna(subset=['Power (kW)'])

print("Cleaned Data Head:")
print(df_clean.head())

# 6. Plot Time against Power
plt.figure(figsize=(10, 5))
plt.plot(df_clean['Time'], df_clean['Power (kW)'], marker='o', linestyle='-')
plt.xlabel('Time')
plt.ylabel('Power (kW)')
plt.title('Commercial Power Load - 2017-01-01')
plt.xticks(rotation=45)
plt.tight_layout()
plt.show()