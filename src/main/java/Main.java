import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.sql.SQLClient;
import io.vertx.ext.sql.SQLConnection;

public class Main {

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        JsonObject config = new JsonObject()
                .put("url", "jdbc:postgresql://localhost:5432/postgres")
                .put("driver_class", "org.postgresql.Driver")
                .put("user", "postgres")
                .put("password", "example");
        SQLClient client = JDBCClient.createShared(vertx, config);

        client.getConnection(res -> {
           if (res.succeeded()) {
               String sql =
                       "select jsonb_set( \n" +
                       "    '{\"key\": \"value\"}'::jsonb, \n" +
                       "    '{key}'::text[], \n" +
                       "    to_jsonb(?::text)" +
                       "  )->>'key' as date";
               SQLConnection conn = res.result();
               String inserted = "2019-10-03";
               JsonArray params = new JsonArray()
                       .add(inserted);
               conn.queryWithParams(sql, params, res2 -> {
                   if (res2.succeeded()) {
                       ResultSet rs = res2.result();
                       String returned = rs.getResults().get(0).getString(0);
                       System.out.println("Expected: " + inserted + " Result: " + returned);
                       conn.close();
                       client.close();
                   }
               });
           }
        });
    }
}
