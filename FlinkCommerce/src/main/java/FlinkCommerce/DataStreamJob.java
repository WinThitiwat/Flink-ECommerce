/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package FlinkCommerce;

import Deserializer.JSONValueDeserializationSchema;
import Dto.*;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.connector.elasticsearch.sink.Elasticsearch7SinkBuilder;
import org.apache.flink.connector.jdbc.JdbcConnectionOptions;
import org.apache.flink.connector.jdbc.JdbcExecutionOptions;
import org.apache.flink.connector.jdbc.JdbcStatementBuilder;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.elasticsearch7.shaded.org.apache.http.HttpHost;
import org.apache.flink.elasticsearch7.shaded.org.elasticsearch.action.index.IndexRequest;
import org.apache.flink.elasticsearch7.shaded.org.elasticsearch.client.Requests;
import org.apache.flink.elasticsearch7.shaded.org.elasticsearch.common.xcontent.XContentType;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.connector.jdbc.JdbcSink;
import java.sql.Date;

import static Utils.JsonUtil.convertTransactionToJson;

/**
 * Skeleton for a Flink DataStream Job.
 *
 * <p>For a tutorial how to write a Flink application, check the
 * tutorials and examples on the <a href="https://flink.apache.org">Flink Website</a>.
 *
 * <p>To package your application into a JAR file for execution, run
 * 'mvn clean package' on the command line.
 *
 * <p>If you change the name of the main class (with the public static void main(String[] args))
 * method, change the respective entry in the POM.xml file (simply search for 'mainClass').
 */
public class DataStreamJob {
	private static final String jdbcUrl = "jdbc:postgresql://localhost:5432/postgres";
	private static final String username = "postgres";
	private static final String password = "postgres";

	public static void main(String[] args) throws Exception {
		// Sets up the execution environment, which is the main entry point
		// to building Flink applications.
		final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

		String topic = "financial_transaction";

		KafkaSource<Transaction> source = KafkaSource.<Transaction>builder()
				.setBootstrapServers("localhost:9092")
				.setTopics(topic)
				.setGroupId("flink-group")
				.setStartingOffsets(OffsetsInitializer.earliest())
				.setValueOnlyDeserializer(new JSONValueDeserializationSchema())
				.build();

		DataStream<Transaction> transactionDataStream = env.fromSource(source, WatermarkStrategy.noWatermarks(), "Kafka Source");

		transactionDataStream.print();

		JdbcExecutionOptions execOption = new JdbcExecutionOptions.Builder()
				.withBatchSize(1000)
				.withBatchIntervalMs(200)
				.withMaxRetries(5)
				.build();

		JdbcConnectionOptions connOption = new JdbcConnectionOptions.JdbcConnectionOptionsBuilder()
				.withUrl(jdbcUrl)
				.withDriverName("org.postgresql.Driver")
				.withUsername(username)
				.withPassword(password)
				.build();

		// ========================= CREATE TABLES ==================================
		// create transaction table
		transactionDataStream.addSink(JdbcSink.sink(
				DataModelHelper.CREATE_TRANSACTIONS_TBL,
				(JdbcStatementBuilder<Transaction>) (preparedStatement, transaction) -> {},
				execOption,
				connOption
		)).name("Create transactions table sink");

		// create sales_per_category table sink
		transactionDataStream.addSink(JdbcSink.sink(
			DataModelHelper.CREATE_SALES_PER_CATEGORY_TBL,
				(JdbcStatementBuilder<Transaction>) (preparedStatement, transaction) -> {},
				execOption,
				connOption
		)).name("Create sales_per_category table sink");

		// create sales_per_day table sink
		transactionDataStream.addSink(JdbcSink.sink(
			DataModelHelper.CREATE_SALES_PER_DAY_TBL,
				(JdbcStatementBuilder<Transaction>) (preparedStatement, transaction) -> {},
				execOption,
				connOption
		)).name("Create sales_per_day table sink");

		// create sales_per_month table sink
		transactionDataStream.addSink(JdbcSink.sink(
			DataModelHelper.CREATE_SALES_PER_MONTH_TBL,
				(JdbcStatementBuilder<Transaction>) (preparedStatement, transaction) -> {},
				execOption,
				connOption
		)).name("Create sales_per_month table sink");


		// ========================= INSERT DATA ==============================
		// Insert transactions data
		transactionDataStream.addSink(JdbcSink.sink(
				DataModelHelper.INSERT_TRANSACTIONS_TBL,
				(JdbcStatementBuilder<Transaction>) (preparedStatement, transaction) -> {
					preparedStatement.setString(1, transaction.getTransactionId());
					preparedStatement.setString(2, transaction.getProductId());
					preparedStatement.setString(3, transaction.getProductName());
					preparedStatement.setString(4, transaction.getProductCategory());
					preparedStatement.setDouble(5, transaction.getProductPrice());
					preparedStatement.setInt(6, transaction.getProductQuantity());
					preparedStatement.setString(7, transaction.getProductBrand());
					preparedStatement.setDouble(8, transaction.getTotalAmount());
					preparedStatement.setString(9, transaction.getCurrency());
					preparedStatement.setString(10, transaction.getCustomerId());
					preparedStatement.setTimestamp(11, transaction.getTransactionDate());
					preparedStatement.setString(12, transaction.getPaymentMethod());
				},
				execOption,
				connOption
		)).name("Insert into transactions table sink");

		// INSERT sales_per_category
		transactionDataStream
				.map(
					transaction -> {
						Date transactionDate = new Date(System.currentTimeMillis());
						String category = transaction.getProductCategory();
						double totalSales = transaction.getTotalAmount();
						return new SalesPerCategory(transactionDate, category, totalSales);
					}
				).keyBy(SalesPerCategory::getCategory)
				.reduce((salesPerCategory, t1) -> {
					salesPerCategory.setTotalSales(salesPerCategory.getTotalSales() + t1.getTotalSales());
					return salesPerCategory;
				}).addSink(JdbcSink.sink(
						DataModelHelper.INSERT_SALES_PER_CATEGORY_TBL,
						(JdbcStatementBuilder<SalesPerCategory>) (preparedStatement, salesPerCategory) -> {
							preparedStatement.setDate(1, new Date(System.currentTimeMillis()));
							preparedStatement.setString(2, salesPerCategory.getCategory());
							preparedStatement.setDouble(3, salesPerCategory.getTotalSales());
						},
						execOption,
						connOption
				)).name("Insert into sales_per_category table sink");

		// INSERT sales_per_day
		transactionDataStream
				.map(
						transaction -> {
							Date transactionDate = new Date(System.currentTimeMillis());
							double totalSales = transaction.getTotalAmount();
							return new SalesPerDay(transactionDate, totalSales);
						}
				).keyBy(SalesPerDay::getTransactionDate)
				.reduce((salesPerDay, t1) -> {
					salesPerDay.setTotalSales(salesPerDay.getTotalSales() + t1.getTotalSales());
					return salesPerDay;
				}).addSink(JdbcSink.sink(
						DataModelHelper.INSERT_SALES_PER_DAY_TBL,
						(JdbcStatementBuilder<SalesPerDay>) (preparedStatement, salesPerDay) -> {
							preparedStatement.setDate(1, new Date(System.currentTimeMillis()));
							preparedStatement.setDouble(2, salesPerDay.getTotalSales());
						},
						execOption,
						connOption
				)).name("Insert into sales_per_day table sink");

		// INSERT sales_per_month
		transactionDataStream
				.map(
						transaction -> {
							Date transactionDate = new Date(System.currentTimeMillis());
							int month = transactionDate.toLocalDate().getMonthValue();
							int year = transactionDate.toLocalDate().getYear();
							double totalSales = transaction.getTotalAmount();
							return new SalesPerMonth(year, month, totalSales);
						}
				).keyBy(SalesPerMonth::getMonth)
				.reduce((salesPerMonth, t1) -> {
					salesPerMonth.setTotalSales(salesPerMonth.getTotalSales() + t1.getTotalSales());
					return salesPerMonth;
				}).addSink(JdbcSink.sink(
						DataModelHelper.INSERT_SALES_PER_MONTH_TBL,
						(JdbcStatementBuilder<SalesPerMonth>) (preparedStatement, salesPerMonth) -> {
							preparedStatement.setInt(1, salesPerMonth.getYear());
							preparedStatement.setInt(2, salesPerMonth.getMonth());
							preparedStatement.setDouble(3, salesPerMonth.getTotalSales());
						},
						execOption,
						connOption
				)).name("Insert into sales_per_month table sink");

		transactionDataStream.sinkTo(
				new Elasticsearch7SinkBuilder<Transaction>()
						.setHosts(new HttpHost("localhost", 9200, "http"))
						.setEmitter((transaction, runtimeContext, requestIndexer) -> {
									String json = convertTransactionToJson(transaction);
									IndexRequest indexRequest = Requests.indexRequest()
											.index("transactions")
											.id(transaction.getTransactionId())
											.source(json, XContentType.JSON);

									requestIndexer.add(indexRequest);
						})
						.build()
		).name("ElasticSearch Sink");


		/*
		 * Here, you can start creating your execution plan for Flink.
		 *
		 * Start with getting some data from the environment, like
		 * 	env.fromSequence(1, 10);
		 *
		 * then, transform the resulting DataStream<Long> using operations
		 * like
		 * 	.filter()
		 * 	.flatMap()
		 * 	.window()
		 * 	.process()
		 *
		 * and many more.
		 * Have a look at the programming guide:
		 *
		 * https://nightlies.apache.org/flink/flink-docs-stable/
		 *
		 */

		// Execute program, beginning computation.
		env.execute("Flink Ecommerce Real-time streaming");
	}
}
