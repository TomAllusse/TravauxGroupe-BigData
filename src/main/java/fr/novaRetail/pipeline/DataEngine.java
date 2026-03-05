package fr.novaRetail.pipeline;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SaveMode;
import org.apache.spark.sql.SparkSession;
import static org.apache.spark.sql.functions.desc;

import java.util.Properties;

public class DataEngine {
    public static void runPipeline(){
        System.out.println("============================================");
        System.out.println("INITIALISATION");
        System.out.println("============================================");

        /* set Variable Globale Java (Windows Only) */
        System.setProperty("hadoop.home.dir", "C:\\Hadoop\\hadoop-3.3.6");
        System.load("C:\\Hadoop\\hadoop-3.3.6\\bin\\hadoop.dll");

        /* Init de Spark */
        SparkSession spark = SparkSession.builder()
                .appName("Spark")
                .config("spark.master", "local[*]")
                .getOrCreate();

        /* Connexion a mySQL */
        String url = "jdbc:mysql://localhost:3306/novaretail_legacy";
        String user = "root";
        String password = "";

        Properties connectionProp = new Properties();
        connectionProp.put("user", user);
        connectionProp.put("password", password);
        connectionProp.put("driver", "com.mysql.cj.jdbc.Driver");

        System.out.println("============================================");
        System.out.println("CONNEXION DB MYSQL");
        System.out.println("============================================");

        Dataset<Row> dfMySQL = spark.read().jdbc(url, "customer_transactions", connectionProp);

        /* tout affiché */
        dfMySQL.printSchema();
        dfMySQL.show();

        System.out.println("============================================");
        System.out.println("NETTOYAGE");
        System.out.println("============================================");

        Dataset<Row> dfMap = dfMySQL
                .select("transaction_id","customer_name", "customer_email", "customer_age", "country", "purchase_amount", "clearance_level")
                .filter(dfMySQL.col("country").isNotNull());

        dfMap.show(5);

        Dataset<Row> dfReduce = dfMap.drop("customer_email").orderBy(desc("country"), desc("purchase_amount"));

        dfReduce.show(5);

        /* Exporté la donnée */
        System.out.println("============================================");
        System.out.println("EXPORT");
        System.out.println("============================================");

        String cheminExportJSON = "archives_logs_country_transactions";
        dfReduce.write()
                .mode(SaveMode.Overwrite)
                .partitionBy("country")
                .json(cheminExportJSON);


        System.out.println("Export réaliser : " + cheminExportJSON);

        /* Vidé la variable spark */
        spark.stop();
    }
}
