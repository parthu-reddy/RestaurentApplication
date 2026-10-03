package com.fooddelivery.restaurant;

import org.junit.jupiter.api.Test;
import java.sql.*;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

/** Explicit PostgreSQL guard against an isolated unit database; never point at Dev. */
class RestaurantOrganisationSchemaIT {
    @Test void freshSchemaRequiresOrganisationAndEnforcesOneBrandPerOrganisation() throws Exception {
        String url = System.getProperty("bp.pg.url");
        assertNotNull(url,"Supply an isolated PostgreSQL unit database with -Dbp.pg.url");
        String schema = "bp_o2_" + UUID.randomUUID().toString().replace("-", "");
        try (Connection connection = DriverManager.getConnection(url,System.getProperty("bp.pg.user","bp_o2"),"");
                Statement sql = connection.createStatement()) {
            try {
                sql.execute("CREATE SCHEMA " + schema);
                sql.execute("SET search_path TO " + schema);
                String schemaSql;
                try (var source = getClass().getResourceAsStream("/db/migration/V1__init_schema.sql")) {
                    assertNotNull(source);schemaSql=new String(source.readAllBytes(),StandardCharsets.UTF_8);
                }
                // Use the exact brands definition; the full schema is verified separately with PostGIS.
                int start=schemaSql.indexOf("CREATE TABLE brands (");
                int end=schemaSql.indexOf("\n);",start)+3;
                assertTrue(start>=0 && end>start);
                sql.execute(schemaSql.substring(start,end));
                var index=java.util.regex.Pattern.compile("CREATE UNIQUE INDEX uq_brands_organisation[^;]+;").matcher(schemaSql);
                assertTrue(index.find());sql.execute(index.group());
                var missing = assertThrows(SQLException.class,()->sql.execute("INSERT INTO brands(id,name) VALUES ('"+UUID.randomUUID()+"','Missing org')"));
                assertEquals("23502",missing.getSQLState());
                UUID organisation = UUID.randomUUID();
                sql.execute("INSERT INTO brands(id,organisation_id,name) VALUES ('"+UUID.randomUUID()+"','"+organisation+"','Test brand')");
                var duplicate = assertThrows(SQLException.class,()->sql.execute("INSERT INTO brands(id,organisation_id,name) VALUES ('"+UUID.randomUUID()+"','"+organisation+"','Test brand')"));
                assertEquals("23505",duplicate.getSQLState());
                assertTrue(duplicate.getMessage().contains("uq_brands_organisation"));
                try (var columns=sql.executeQuery("SELECT count(*) FROM information_schema.columns WHERE table_schema='"+schema+"' AND table_name='brands' AND column_name='owner_id'")) {
                    assertTrue(columns.next());assertEquals(0,columns.getInt(1));
                }
            } finally { sql.execute("DROP SCHEMA IF EXISTS " + schema + " CASCADE"); }
        }
    }
}
