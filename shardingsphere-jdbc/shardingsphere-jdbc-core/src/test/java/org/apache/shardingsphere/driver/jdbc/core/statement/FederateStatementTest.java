/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.shardingsphere.driver.jdbc.core.statement;

import org.apache.shardingsphere.driver.jdbc.base.AbstractShardingSphereDataSourceForFederateTest;
import org.junit.Test;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public final class FederateStatementTest extends AbstractShardingSphereDataSourceForFederateTest {

    private static final String SELECT_SQL_BY_ID_ACROSS_SINGLE_TABLES =
            "select o.*, i.* from t_order_federate o, t_order_item_federate i "
            + "where o.order_id = 1000 and i.item_id = 100000";

    private static final String SELECT_SQL_BY_ID_ACROSS_SINGLE_AND_SHARDING_TABLES =
            "select t_order_federate.*, t_order_item_federate_sharding.* from t_order_federate, "
                    + "t_order_item_federate_sharding where t_order_federate.order_id = "
                    + "t_order_item_federate_sharding.item_id";

    private static final String SELECT_SQL_BY_ID_ACROSS_SINGLE_AND_SHARDING_TABLES_ALIAS = "select o.*, i.* from"
            + " t_order_federate o, t_order_item_federate_sharding i where o.order_id = i.item_id";

    private static final String SELECT_SQL_BY_ID_ACROSS_SINGLE_AND_SHARDING_TABLES_REWRITE =
            "select t_order_federate.*, t_order_item_federate_sharding.* from "
                    + "t_order_federate, t_order_item_federate_sharding "
                    + "where t_order_federate.order_id = t_order_item_federate_sharding.item_id "
                    + "AND t_order_item_federate_sharding.remarks = 't_order_item_federate_sharding'";

    private static final String SELECT_SQL_BY_ID_ACROSS_SINGLE_AND_SHARDING_TABLES_ORDER_BY =
            "select t_order_federate.* from t_order_federate, t_order_item_federate_sharding "
                    + "where t_order_federate.order_id = t_order_item_federate_sharding.item_id "
                    + "ORDER BY t_order_item_federate_sharding.user_id";

    private static final String SELECT_SQL_BY_ID_ACROSS_SINGLE_TABLES_WITH_ENCRYPT =
            "select t_user_encrypt_federate.user_id, t_user_encrypt_federate.pwd, t_user_info.information from t_user_encrypt_federate, t_user_info "
            + "where t_user_encrypt_federate.user_id = t_user_info.user_id ";

    private static final String SELECT_SQL_BY_ID_ACROSS_SINGLE_AND_SHARDING_TABLES_WITH_ENCRYPT =
            "select t_user_encrypt_federate_sharding.user_id, t_user_encrypt_federate_sharding.pwd, t_user_info.information from t_user_encrypt_federate_sharding, t_user_info "
                    + "where t_user_encrypt_federate_sharding.user_id = t_user_info.user_id ";

    private static final String SELECT_SQL_BY_ID_ACROSS_TWO_SHARDING_TABLES =
            "select o.order_id_sharding, i.order_id from t_order_federate_sharding o, t_order_item_federate_sharding i "
                    + "where o.order_id_sharding = i.item_id";

    @Test
    public void assertQueryWithFederateInSingleTables() throws SQLException {
        ShardingSphereStatement preparedStatement = (ShardingSphereStatement) getShardingSphereDataSource().getConnection().createStatement();
        ResultSet resultSet = preparedStatement.executeQuery(SELECT_SQL_BY_ID_ACROSS_SINGLE_TABLES);
        assertNotNull(resultSet);
        assertTrue(resultSet.next());
        assertThat(resultSet.getInt(1), is(1000));
        assertThat(resultSet.getInt(2), is(10));
        assertThat(resultSet.getString(3), is("init"));
        assertThat(resultSet.getInt(4), is(100000));
        assertThat(resultSet.getInt(5), is(1000));
        assertThat(resultSet.getInt(6), is(10));
        assertThat(resultSet.getString(7), is("init"));
        assertFalse(resultSet.next());
    }

    @Test
    public void assertQueryWithFederateInSingleAndShardingTable() throws SQLException {
        ShardingSphereStatement preparedStatement = (ShardingSphereStatement) getShardingSphereDataSource().getConnection().createStatement();
        ResultSet resultSet = preparedStatement.executeQuery(SELECT_SQL_BY_ID_ACROSS_SINGLE_AND_SHARDING_TABLES);
        assertNotNull(resultSet);
        assertTrue(resultSet.next());
        assertThat(resultSet.getInt(1), is(1000));
        assertThat(resultSet.getInt(2), is(10));
        assertThat(resultSet.getString(3), is("init"));
        assertThat(resultSet.getInt(4), is(1000));
        assertThat(resultSet.getInt(5), is(10000));
        assertTrue(resultSet.next());
        assertThat(resultSet.getInt(1), is(1001));
        assertThat(resultSet.getInt(2), is(11));
        assertThat(resultSet.getString(3), is("init"));
        assertThat(resultSet.getInt(4), is(1001));
        assertThat(resultSet.getInt(5), is(10001));
        assertFalse(resultSet.next());
    }

    @Test
    public void assertQueryWithFederateInSingleAndShardingTableWithAlias() throws SQLException {
        ShardingSphereStatement preparedStatement = (ShardingSphereStatement) getShardingSphereDataSource().getConnection().createStatement();
        ResultSet resultSet = preparedStatement.executeQuery(SELECT_SQL_BY_ID_ACROSS_SINGLE_AND_SHARDING_TABLES_ALIAS);
        assertNotNull(resultSet);
        assertTrue(resultSet.next());
        assertThat(resultSet.getInt(1), is(1000));
        assertThat(resultSet.getInt(2), is(10));
        assertThat(resultSet.getString(3), is("init"));
        assertThat(resultSet.getInt(4), is(1000));
        assertThat(resultSet.getInt(5), is(10000));
        assertTrue(resultSet.next());
        assertThat(resultSet.getInt(1), is(1001));
        assertThat(resultSet.getInt(2), is(11));
        assertThat(resultSet.getString(3), is("init"));
        assertThat(resultSet.getInt(4), is(1001));
        assertThat(resultSet.getInt(5), is(10001));
        assertFalse(resultSet.next());
    }

    @Test
    public void assertQueryWithFederateInSingleAndShardingTableRewrite() throws SQLException {
        ShardingSphereStatement preparedStatement = (ShardingSphereStatement) getShardingSphereDataSource().getConnection().createStatement();
        ResultSet resultSet = preparedStatement.executeQuery(SELECT_SQL_BY_ID_ACROSS_SINGLE_AND_SHARDING_TABLES_REWRITE);
        assertNotNull(resultSet);
        assertTrue(resultSet.next());
        assertThat(resultSet.getInt(1), is(1000));
        assertThat(resultSet.getInt(2), is(10));
        assertThat(resultSet.getString(3), is("init"));
        assertThat(resultSet.getInt(4), is(1000));
        assertThat(resultSet.getInt(5), is(10000));
        assertTrue(resultSet.next());
        assertThat(resultSet.getInt(1), is(1001));
        assertThat(resultSet.getInt(2), is(11));
        assertThat(resultSet.getString(3), is("init"));
        assertThat(resultSet.getInt(4), is(1001));
        assertThat(resultSet.getInt(5), is(10001));
        assertFalse(resultSet.next());
    }

    @Test
    public void assertQueryWithFederateInSingleAndShardingTableOrderBy() throws SQLException {
        ShardingSphereStatement preparedStatement = (ShardingSphereStatement) getShardingSphereDataSource().getConnection().createStatement();
        ResultSet resultSet = preparedStatement.executeQuery(SELECT_SQL_BY_ID_ACROSS_SINGLE_AND_SHARDING_TABLES_ORDER_BY);
        assertNotNull(resultSet);
        assertTrue(resultSet.next());
        assertThat(resultSet.getInt(1), is(1000));
        assertThat(resultSet.getInt(2), is(10));
        assertThat(resultSet.getString(3), is("init"));
        assertTrue(resultSet.next());
        assertThat(resultSet.getInt(1), is(1001));
        assertThat(resultSet.getInt(2), is(11));
        assertThat(resultSet.getString(3), is("init"));
        assertFalse(resultSet.next());
    }

    @Test
    public void assertQueryWithFederateInSingleTablesWithEncryptRule() throws SQLException {
        ShardingSphereStatement preparedStatement = (ShardingSphereStatement) getShardingSphereDataSource().getConnection().createStatement();
        ResultSet resultSet = preparedStatement.executeQuery(SELECT_SQL_BY_ID_ACROSS_SINGLE_TABLES_WITH_ENCRYPT);
        assertNotNull(resultSet);
        assertTrue(resultSet.next());
        assertThat(resultSet.getInt(1), is(0));
        assertThat(resultSet.getString(2), is("decryptValue"));
        assertThat(resultSet.getString(3), is("description0"));
        assertTrue(resultSet.next());
        assertThat(resultSet.getInt(1), is(1));
        assertThat(resultSet.getString(2), is("decryptValue"));
        assertThat(resultSet.getString(3), is("description1"));
        assertTrue(resultSet.next());
        assertThat(resultSet.getInt(1), is(2));
        assertThat(resultSet.getString(2), is("decryptValue"));
        assertThat(resultSet.getString(3), is("description2"));
        assertTrue(resultSet.next());
        assertThat(resultSet.getInt(1), is(3));
        assertThat(resultSet.getString(2), is("decryptValue"));
        assertThat(resultSet.getString(3), is("description3"));
        assertFalse(resultSet.next());
    }

    @Test
    public void assertQueryWithFederateInSingleAndShardingTablesWithEncryptRule() throws SQLException {
        ShardingSphereStatement preparedStatement = (ShardingSphereStatement) getShardingSphereDataSource().getConnection().createStatement();
        ResultSet resultSet = preparedStatement.executeQuery(SELECT_SQL_BY_ID_ACROSS_SINGLE_AND_SHARDING_TABLES_WITH_ENCRYPT);
        assertNotNull(resultSet);
        assertTrue(resultSet.next());
        assertThat(resultSet.getInt(1), is(0));
        assertThat(resultSet.getString(2), is("decryptValue"));
        assertThat(resultSet.getString(3), is("description0"));
        assertTrue(resultSet.next());
        assertThat(resultSet.getInt(1), is(1));
        assertThat(resultSet.getString(2), is("decryptValue"));
        assertThat(resultSet.getString(3), is("description1"));
        assertTrue(resultSet.next());
        assertThat(resultSet.getInt(1), is(2));
        assertThat(resultSet.getString(2), is("decryptValue"));
        assertThat(resultSet.getString(3), is("description2"));
        assertTrue(resultSet.next());
        assertThat(resultSet.getInt(1), is(3));
        assertThat(resultSet.getString(2), is("decryptValue"));
        assertThat(resultSet.getString(3), is("description3"));
        assertFalse(resultSet.next());
    }

    @Test
    public void assertQueryWithFederateBetweenTwoShardingTables() throws SQLException {
        ShardingSphereStatement preparedStatement = (ShardingSphereStatement) getShardingSphereDataSource().getConnection().createStatement();
        ResultSet resultSet = preparedStatement.executeQuery(SELECT_SQL_BY_ID_ACROSS_TWO_SHARDING_TABLES);
        assertNotNull(resultSet);
        assertTrue(resultSet.next());
        assertThat(resultSet.getInt(1), is(1010));
        assertThat(resultSet.getInt(2), is(10001));
        assertTrue(resultSet.next());
        assertThat(resultSet.getInt(1), is(1011));
        assertThat(resultSet.getInt(2), is(10001));
        assertFalse(resultSet.next());
    }
}
