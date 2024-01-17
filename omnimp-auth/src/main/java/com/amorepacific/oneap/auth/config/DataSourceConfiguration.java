/*
 * <pre>
 * Copyright (c) 2020 Amore Pacific.
 * All right reserved.
 *
 * This software is the confidential and proprietary information of Amore
 * Pacific. You shall not disclose such Confidential Information and
 * shall use it only in accordance with the terms of the license agreement
 * you entered into with Amore Pacific.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Author	          : hjw0228
 * Date   	          : 2020. 10. 21..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.auth.config;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
//import org.springframework.jdbc.datasource.DataSourceTransactionManager;
//import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.amorepacific.oneap.auth.config.DataSourceHikariProperties;
import com.amorepacific.oneap.auth.config.DataSourceReadProperties;
import com.amorepacific.oneap.auth.config.DataSourceWriteProperties;
import com.amorepacific.oneap.common.datasource.LazyReplicationConnectionDataSourceProxy;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

/**
 * <pre>
 * com.amorepacific.oneap.auth.config 
 *    |_ DataSourceConfiguration.java
 * </pre>
 *
 * @desc    :
 * @date    : 2020. 10. 21.
 * @version : 1.0
 * @author  : hjw0228
 */

@Configuration
@EnableTransactionManagement
public class DataSourceConfiguration {

	@Value("${spring.datasource.hikari.maximum-pool-size}")
	private String MAXIMUM_POOL_SIZE;
	
    @Value("${mybatis.mapper-locations}")
	private String MYBATIS_MAPPER_LOCATIONS;
    
    @Value("${mybatis.configuration.map-underscore-to-camel-case}")
    private String MYBATIS_CONFIGURATION_MAP_UNDERSCORE_TO_CAMEL_CASE;

    @Autowired
    DataSourceWriteProperties dataSourceWriteProperties;
    
    @Autowired
    DataSourceReadProperties dataSourceReadProperties;
    
    @Autowired
    DataSourceHikariProperties dataSourceHikariProperties;

    @Primary
    @Bean(name = "readWriteDataSource")
    public DataSource readWriteDataSource() throws SQLException {

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(dataSourceWriteProperties.getUrl());
        config.setDriverClassName(dataSourceWriteProperties.getDriverClassName());
        config.setUsername(dataSourceWriteProperties.getUsername());
        config.setPassword(dataSourceWriteProperties.getPassword());
        config.setPoolName("auth-read-write");
        config.setMinimumIdle(Integer.valueOf(dataSourceHikariProperties.getMinimumIdle()));
        config.setMaximumPoolSize(Integer.valueOf(dataSourceHikariProperties.getMaximumPoolSize()));
        config.setMaxLifetime(Integer.valueOf(dataSourceHikariProperties.getMaxLifetime()));
        config.setIdleTimeout(Integer.valueOf(dataSourceHikariProperties.getIdleTimeout()));
        config.setConnectionTimeout(Integer.valueOf(dataSourceHikariProperties.getConnectionTimeout()));
        config.setValidationTimeout(Integer.valueOf(dataSourceHikariProperties.getValidationTimeout()));

        HikariDataSource dataSource = new HikariDataSource(config);

        return dataSource;
    }

    @Bean(name = "readOnlyDataSource")
    public DataSource slaveDataSource() {

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(dataSourceReadProperties.getUrl());
        config.setDriverClassName(dataSourceReadProperties.getDriverClassName());
        config.setUsername(dataSourceReadProperties.getUsername());
        config.setPassword(dataSourceReadProperties.getPassword());
        config.setPoolName("auth-read-only");
        config.setMinimumIdle(Integer.valueOf(dataSourceHikariProperties.getMinimumIdle()));
        config.setMaximumPoolSize(Integer.valueOf(dataSourceHikariProperties.getMaximumPoolSize()));
        config.setMaxLifetime(Integer.valueOf(dataSourceHikariProperties.getMaxLifetime()));
        config.setIdleTimeout(Integer.valueOf(dataSourceHikariProperties.getIdleTimeout()));
        config.setConnectionTimeout(Integer.valueOf(dataSourceHikariProperties.getConnectionTimeout()));
        config.setValidationTimeout(Integer.valueOf(dataSourceHikariProperties.getValidationTimeout()));

        HikariDataSource dataSource = new HikariDataSource(config);

        return dataSource;
    }

    @Bean(name = "dataSource")
    public DataSource dataSource(@Qualifier("readWriteDataSource") DataSource readWriteDataSource, @Qualifier("readOnlyDataSource") DataSource readOnlyDataSource) {

        return new LazyReplicationConnectionDataSourceProxy(readWriteDataSource, readOnlyDataSource);
    }

	/*
	 * @Bean(name = "transactionManager") public PlatformTransactionManager transactionManager(@Qualifier("dataSource") DataSource dataSource) {
	 * 
	 * return new DataSourceTransactionManager(dataSource); }
	 */

    @Bean(name = "sqlSessionFactory")
    public SqlSessionFactory sqlSessionFactory(@Qualifier("dataSource") DataSource dataSource, ApplicationContext applicationContext) throws Exception {

        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSource);
        org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
        configuration.setMapUnderscoreToCamelCase(true);
        sqlSessionFactoryBean.setConfiguration(configuration);
        sqlSessionFactoryBean.setMapperLocations(applicationContext.getResources(MYBATIS_MAPPER_LOCATIONS));

        return sqlSessionFactoryBean.getObject();
    }
 
    @Bean(name = "sqlSessionTemplate")
    public SqlSessionTemplate sqlSessionTemplate(@Qualifier("sqlSessionFactory") SqlSessionFactory sqlSessionFactory) throws Exception {

        return new SqlSessionTemplate(sqlSessionFactory);
    }
}
