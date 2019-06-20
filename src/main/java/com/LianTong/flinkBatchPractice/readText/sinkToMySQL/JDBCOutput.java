package com.LianTong.flinkBatchPractice.readText.sinkToMySQL;

import org.apache.flink.api.common.io.RichOutputFormat;
import org.apache.flink.api.java.tuple.Tuple5;
import org.apache.flink.configuration.Configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * \* Created with IntelliJ IDEA.
 * \* User: Aji
 * \* Date: 2019/6/20
 * \* Time: 9:04
 * \* Description: This method is used to store formatted DataSets
 *                 Tuple5<String, String, BigDecimal, BigDecimal, BigDecimal>
 *                 into MySQL.
 */

public class JDBCOutput extends RichOutputFormat<Tuple5<String, String, BigDecimal, BigDecimal, BigDecimal>> {
    private static final long serialVersionUID = 1L;
    private static final int DEFAULT_BATCH_INTERVAL = 5000;

    private static final Logger LOG = LoggerFactory.getLogger(org.apache.flink.api.java.io.jdbc.JDBCOutputFormat.class);

    private Connection dbConn;
    private PreparedStatement upload;

    private int batchCount = 0;

    private int[] typesArray;
    /**
     * Configures this output format. Since output formats are instantiated generically and hence parameterless,
     * this method is the place where the output formats set their basic fields based on configuration values.
     * <p>
     * This method is always called first on a newly instantiated output format.
     *
     * @param parameters The configuration with all parameters.
     */
    @Override
    public void configure(Configuration parameters) {
    }

    /**
     * Opens a parallel instance of the output format to store the result of its parallel instance.
     * <p>
     * When this method is called, the output format it guaranteed to be configured.
     *
     * @param taskNumber The number of the parallel instance.
     * @param numTasks The number of parallel tasks.
     * @throws IOException Thrown, if the output could not be opened due to an I/O problem.
     */
    @Override
    public void open(int taskNumber, int numTasks) throws IOException {
        try {
            establishConnection();
            String query = "INSERT INTO 省分_产品id(省分, 产品ID, 总数, arup值总和,平均arup值) VALUES(?,?,?,?,?)";
            upload = dbConn.prepareStatement(query);
        } catch (SQLException sqe) {
            throw new IllegalArgumentException("open() failed.", sqe);
        } catch (ClassNotFoundException cnfe) {
            throw new IllegalArgumentException("JDBC driver class not found.", cnfe);
        }
    }

    private void establishConnection() throws SQLException, ClassNotFoundException {
        String drivername = "com.mysql.cj.jdbc.Driver";
        Class.forName(drivername);
        String dbURL = "jdbc:mysql://localhost:3306/test1?serverTimezone=UTC&useSSL=false";
        String username = "root";
        String password = "Pass1996!";
        dbConn = DriverManager.getConnection(dbURL, username, password);

    }

    /**
     * Adds a record to the prepared statement.
     *
     * <p>When this method is called, the output format is guaranteed to be opened.
     *
     * <p>WARNING: this may fail when no column types specified (because a best effort approach is attempted in order to
     * insert a null value but it's not guaranteed that the JDBC driver handles PreparedStatement.setObject(pos, null))
     *
     * @param row The records to add to the output.
     * @see PreparedStatement
     * @throws IOException Thrown, if the records could not be added due to an I/O problem.
     */
    @Override
    public void writeRecord(Tuple5<String, String, BigDecimal, BigDecimal, BigDecimal> row) throws IOException {

        if (typesArray != null && typesArray.length > 0 && typesArray.length != row.getArity()) {
            LOG.warn("Column SQL types array doesn't match arity of passed Row! Check the passed array...");
        }
        try {

            if (typesArray == null) {
                // no types provided
                for (int index = 0; index < row.getArity(); index++) {
                    LOG.warn("Unknown column type for column {}. Best effort approach to set its value: {}.", index + 1, row.getField(index));
                    upload.setObject(index + 1, row.getField(index));
                }
            } else {
                // types provided
                for (int index = 0; index < row.getArity(); index++) {

                    if (row.getField(index) == null) {
                        upload.setNull(index + 1, typesArray[index]);
                    } else {
                        // casting values as suggested by http://docs.oracle.com/javase/1.5.0/docs/guide/jdbc/getstart/mapping.html
                        switch (typesArray[index]) {
                            case java.sql.Types.NULL:
                                upload.setNull(index + 1, typesArray[index]);
                                break;
                            case java.sql.Types.CHAR:
                            case java.sql.Types.NCHAR:
                            case java.sql.Types.VARCHAR:
                            case java.sql.Types.LONGVARCHAR:
                            case java.sql.Types.LONGNVARCHAR:
                                upload.setString(index + 1, (String) row.getField(index));
                                break;
                            case java.sql.Types.DECIMAL:
                            case java.sql.Types.NUMERIC:
                                upload.setBigDecimal(index + 1, (java.math.BigDecimal) row.getField(index));
                                break;
                            default:
                                upload.setObject(index + 1, row.getField(index));
                                LOG.warn("Unmanaged sql type ({}) for column {}. Best effort approach to set its value: {}.",
                                        typesArray[index], index + 1, row.getField(index));
                        }
                    }
                }
            }
            upload.addBatch();
            batchCount++;
        } catch (SQLException e) {
            throw new RuntimeException("Preparation of JDBC statement failed.", e);
        }

        if (batchCount >= DEFAULT_BATCH_INTERVAL) {
            // execute batch
            flush();
        }
    }

    void flush() {
        try {
            upload.executeBatch();
            batchCount = 0;
        } catch (SQLException e) {
            throw new RuntimeException("Execution of JDBC statement failed.", e);
        }
    }

    int[] getTypesArray() {
        return typesArray;
    }

    /**
     * Method that marks the end of the life-cycle of parallel output instance. Should be used to close
     * channels and streams and release resources.
     * After this method returns without an error, the output is assumed to be correct.
     * <p>
     * When this method is called, the output format it guaranteed to be opened.
     *
     * @throws IOException Thrown, if the input could not be closed properly.
     */
    @Override
    public void close() throws IOException {
        if (upload != null) {
            flush();
            // close the connection
            try {
                upload.close();
            } catch (SQLException e) {
                LOG.info("JDBC statement could not be closed: " + e.getMessage());
            } finally {
                upload = null;
            }
        }

        if (dbConn != null) {
            try {
                dbConn.close();
            } catch (SQLException se) {
                LOG.info("JDBC connection could not be closed: " + se.getMessage());
            } finally {
                dbConn = null;
            }
        }
    }

}
