//package data_access;
//
//import entity.Building;
//import org.junit.jupiter.api.Test;
//
//import java.io.FileWriter;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//class BuildingLoaderTest {
//
//    @Test
//    void testLoadBuildings() throws Exception {
//
//        String jsonContent = """
//        [
//          {
//            "code": "BA",
//            "name": "Bahen Centre",
//            "address": "40 St George St, University of Toronto"
//          },
//          {
//            "code": "SS",
//            "name": "Sidney Smith Hall",
//            "address": "100 St George St, University of Toronto"
//          }
//        ]
//        """;
//
//        Path tempFile = Files.createTempFile("buildings_test", ".json");
//        try (FileWriter writer = new FileWriter(tempFile.toFile())) {
//            writer.write(jsonContent);
//        }
//
//        List<Building> buildings = BuildingLoader.loadBuildings(tempFile.toString());
//
//        assertNotNull(buildings);
//        assertEquals(2, buildings.size());
//
//        Building ba = buildings.get(0);
//        assertEquals("BA", ba.getBuildingCode());
//        assertEquals("Bahen Centre", ba.getName());
//        assertEquals("40 St George St, University of Toronto", ba.getAddress());
//
//        Building ss = buildings.get(1);
//        assertEquals("SS", ss.getBuildingCode());
//        assertEquals("Sidney Smith Hall", ss.getName());
//        assertEquals("100 St George St, University of Toronto", ss.getAddress());
//    }
//}
