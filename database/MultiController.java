package com.userservice.database;

import com.userservice.country.CountryBean;
import com.userservice.country.CountryDao;
import com.userservice.users.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/users")
public class MultiController {

    @Autowired
    MultiDbManager multiDbManager;

    @Autowired
    CountryDao countryDao;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    UserDao userDao;

    @GetMapping("/country/database")
    public Object connectAnotherDatabase() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/lookup?useSSL=false&serverTimezone=UTC";
        String username = "root";
        String password = "123456";
        String driverClassName = "com.mysql.cj.jdbc.Driver";
        multiDbManager.addTenant("Client",url,username,password,driverClassName);
        multiDbManager.setCurrentTenant("Client");

        List<CountryBean> countryListNew = countryDao.findAll();
        System.out.println("countryList: "+countryListNew.size());

        multiDbManager.setCurrentTenant("default");
        List<CountryBean> countryList = countryDao.findAll();
        System.out.println("countryList: "+countryList.size());
       // System.out.println(multiDbManager.getTenantList());

        multiDbManager.setCurrentTenant("Client");

        List<CountryBean> countryList2 = countryDao.findAll();
        System.out.println("countryList: "+countryList2.size());
        return countryListNew;
    }

    @GetMapping("/country/check")
    public Object CombineResult(){
        List<CountryBean> countryBeans = Arrays.asList(Objects.requireNonNull(restTemplate.getForObject("http://USER-SERVICE/users/country/database" , CountryBean[].class)));
       // System.out.println(countryBeans);
        countryBeans.forEach(x->{
            System.out.println(x);
            userDao.findAll().forEach(System.out::println);
        });
        return countryBeans;
    }

    @GetMapping("/country/custom-database")
    public Object customDatabaseConnect() throws SQLException {

        Statement st=(Statement) getConnection2().createStatement();
        String sql="Select * from lu_country";
        ResultSet rs=st.executeQuery(sql);

        while(rs.next())
        {
            System.out.println(rs.getInt(1));
        }
        return "";
    }

    public Connection getConnection() throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.jdbc.Driver");
        return DriverManager.getConnection("jdbc:mysql://184.95.54.139:3306/capacity",
                "tdbusr",
                "IrBLzt-st@20");
    }

    public Connection getConnection2() throws SQLException {
        DataSource dataSource = DataSourceBuilder.create()
                .driverClassName("com.mysql.jdbc.Driver")
                .url("jdbc:mysql://184.95.54.139:3306/capacity")
                .username("tdbusr")
                .password("IrBLzt-st@20")
                .build();
        try(Connection c = dataSource.getConnection()) {
            return c;
        }
    }


}
