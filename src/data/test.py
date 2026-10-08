import glob
import os
import pandas as pd

# 1. Path to the folder containing your xlsx files
folder_path = "data/Electric power load data/2019/30_minutes/2019_30min_Residential"
file_list = glob.glob(os.path.join(folder_path, '*.xlsx'))

dataframes = []

for file in file_list:
    # Read the Time and Power (kW) columns
    df = pd.read_excel(file)
    
    # Optional: Add a column to track which file the data came from
    # df['Source_File'] = os.path.basename(file)
    # Format Time column explicitly as 'YYYY-MM-DD HH:MM:SS'
    if 'Time' in df.columns:
      df['Time'] = pd.to_datetime(df['Time']).dt.strftime('%Y-%m-%d %H:%M:%S')
    
    dataframes.append(df)

# 2. Combine all dataframes into one vertically
combined_df = pd.concat(dataframes, ignore_index=True)

# 3. Save to a single CSV or XLSX file
combined_df.to_csv('2019_30min_Residential.csv', index=False)
# combined_df.to_excel('combined_power_data.xlsx', index=False)