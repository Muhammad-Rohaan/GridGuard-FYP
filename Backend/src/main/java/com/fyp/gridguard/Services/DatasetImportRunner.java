//package com.fyp.gridguard.Services;
//
//import com.fyp.gridguard.Models.Areas;
//import com.fyp.gridguard.Repositories.AreaRepo;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.ApplicationArguments;
//import org.springframework.boot.ApplicationRunner;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.stereotype.Component;
//
//import java.io.BufferedReader;
//import java.io.FileReader;
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//import java.util.ArrayList;
//import java.util.List;
//
//@Component
//@RequiredArgsConstructor
//@Slf4j
//public class DatasetImportRunner implements ApplicationRunner {
//
//    private final JdbcTemplate jdbcTemplate;
//    private final AreaRepo areaRepository;
//
//    @Value("${app.import-dataset:false}")
//    private boolean importDataset;
//
//    @Override
//    public void run(ApplicationArguments args) throws Exception {
//        if (!importDataset) return;
//
//        log.info("Starting CSV import...");
//
//        Areas area = areaRepository.findAll().get(0); // only one area
//        Long areaId = area.getId();
//
//        // Clear old data (optional)
//        jdbcTemplate.update("DELETE FROM load_readings WHERE area_id = ?", areaId);
//
//        String sql = """
//            INSERT INTO load_readings (area_id, recorded_at, load_kw)
//            VALUES (?, ?, ?)
//            ON CONFLICT (area_id, recorded_at) DO NOTHING
//            """;
//
//        try (BufferedReader br = new BufferedReader(
//                new FileReader("src/main/resources/5_min_Residential.csv"))) {
//
//            String line = br.readLine(); // skip header
//            List<Object[]> batch = new ArrayList<>();
//            int count = 0;
//
//            while ((line = br.readLine()) != null) {
//                String[] parts = line.split(",");
//                if (parts.length < 2) continue;
//
//                LocalDateTime time = LocalDateTime.parse(
//                        parts[0].trim(),
//                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
//                );
//                Double power = Double.parseDouble(parts[1].trim());
//
//                batch.add(new Object[]{areaId, time, power});
//
//                if (batch.size() == 5000) {
//                    jdbcTemplate.batchUpdate(sql, batch);
//                    count += batch.size();
//                    batch.clear();
//                    log.info("Imported {} rows...", count);
//                }
//            }
//
//            // remaining rows
//            if (!batch.isEmpty()) {
//                jdbcTemplate.batchUpdate(sql, batch);
//                count += batch.size();
//            }
//
//            log.info("CSV Import finished. Total rows: {}", count);
//        }
//    }
//}
