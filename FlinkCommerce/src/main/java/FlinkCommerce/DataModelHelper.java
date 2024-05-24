package FlinkCommerce;

import org.apache.kafka.common.protocol.types.Field;

public class DataModelHelper {

    public static final String TRANSACTION_TBL = "transactions";
    public static final String CREATE_TRANSACTIONS_TBL = "CREATE TABLE IF NOT EXISTS " + TRANSACTION_TBL + " (" +
            "transaction_id VARCHAR(255) PRIMARY KEY, " +
            "product_id VARCHAR(255), " +
            "product_name VARCHAR(255), " +
            "product_category VARCHAR(255), " +
            "product_price DOUBLE PRECISION, " +
            "product_quantity INTEGER, " +
            "product_brand VARCHAR(255), " +
            "total_amount DOUBLE PRECISION, " +
            "currency VARCHAR(255), " +
            "customer_id VARCHAR(255), " +
            "transaction_date TIMESTAMP, " +
            "payment_method VARCHAR(255) " +
            ")";
    public static final String INSERT_TRANSACTIONS_TBL = "INSERT INTO " + TRANSACTION_TBL +
            " (transaction_id, product_id, product_name, product_category, product_price, " +
            " product_quantity, product_brand, total_amount, currency, customer_id, transaction_date, payment_method )" +
            " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )" +
            " ON CONFLICT (transaction_id) DO UPDATE SET " +
            " product_id = EXCLUDED.product_id, " +
            " product_name = EXCLUDED.product_name, " +
            " product_category = EXCLUDED.product_category, " +
            " product_price = EXCLUDED.product_price, " +
            " product_quantity = EXCLUDED.product_quantity, " +
            " product_brand = EXCLUDED.product_brand, " +
            " total_amount = EXCLUDED.total_amount, " +
            " currency = EXCLUDED.currency, " +
            " customer_id = EXCLUDED.customer_id, " +
            " transaction_date = EXCLUDED.transaction_date, " +
            " payment_method = EXCLUDED.payment_method " +
            " WHERE transactions.transaction_id = EXCLUDED.transaction_id";

    public static final String SALES_PER_CATEGORY_TBL = "sales_per_category";
    public static final String CREATE_SALES_PER_CATEGORY_TBL = "CREATE TABLE IF NOT EXISTS " + SALES_PER_CATEGORY_TBL + " (" +
            " transaction_date DATE, " +
            " category VARCHAR(255), " +
            " total_sales DOUBLE PRECISION, " +
            " PRIMARY KEY (transaction_date, category)" +
            ")";

    public static final String INSERT_SALES_PER_CATEGORY_TBL = "INSERT INTO " + SALES_PER_CATEGORY_TBL +
            " (transaction_date, category, total_sales)" +
            " VALUES (?, ?, ?)" +
            " ON CONFLICT (transaction_date, category) DO UPDATE SET" +
            " total_sales = EXCLUDED.total_sales" +
            " WHERE sales_per_category.transaction_date = EXCLUDED.transaction_date" +
            " AND sales_per_category.category = EXCLUDED.category";

    public static final String SALES_PER_DAY_TBL = "sales_per_day";
    public static final String CREATE_SALES_PER_DAY_TBL = "CREATE TABLE IF NOT EXISTS " + SALES_PER_DAY_TBL + " (" +
            " transaction_date DATE, " +
            " total_sales DOUBLE PRECISION, " +
            " PRIMARY KEY (transaction_date)" +
            ")";

    public static final String INSERT_SALES_PER_DAY_TBL = "INSERT INTO " + SALES_PER_DAY_TBL +
            " (transaction_date, total_sales)" +
            " VALUES (?, ?)" +
            " ON CONFLICT (transaction_date) DO UPDATE SET" +
            " total_sales = EXCLUDED.total_sales" +
            " WHERE sales_per_day.transaction_date = EXCLUDED.transaction_date";
    public static final String SALES_PER_MONTH_TBL = "sales_per_month";
    public static final String CREATE_SALES_PER_MONTH_TBL = "CREATE TABLE IF NOT EXISTS " + SALES_PER_MONTH_TBL + " (" +
            " year INTEGER," +
            " month INTEGER," +
            " total_sales DOUBLE PRECISION, " +
            " PRIMARY KEY (year, month)" +
            ")";
    public static final String INSERT_SALES_PER_MONTH_TBL = "INSERT INTO " + SALES_PER_MONTH_TBL +
            " (year, month, total_sales)" +
            " VALUES (?, ?, ?)" +
            " ON CONFLICT (year, month) DO UPDATE SET " +
            " total_sales = EXCLUDED.total_sales" +
            " WHERE sales_per_month.year = EXCLUDED.year" +
            " AND sales_per_month.month = EXCLUDED.month";
}
