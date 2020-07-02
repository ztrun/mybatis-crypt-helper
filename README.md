# 利用 MyBatis 的 TypeHandler 对数据库字段进行加密

## MyBatis 3.x 使用方法

### Maven
添加 Maven 依赖

```xml
<dependency>
    <groupId>com.huiyadan</groupId>
    <artifactId>mybatis-crypt-helper</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
```

### Mybatis 配置


#### 1. 单独使用 MyBatis
```xml
<!-- mybatis-config.xml -->
<typeAliases>
    <package name="com.huiyadan.crypt.mybatis.alias" />
</typeAliases>

<typeHandlers>
    <package name="com.huiyadan.crypt.mybatis.type" />
</typeHandlers>
```

#### 2. 与 Spring 结合
```java
@Bean
public SqlSessionFactory sqlSessionFactory(Configuration config) {
    SqlSessionFactoryBean factory = new SqlSessionFactoryBean();
    factory.setTypeAliasesPackage("com.huiyadan.crypt.mybatis.alias");
    factory.setTypeHandlersPackage("com.huiyadan.crypt.mybatis.type");
    return factory.getObject();
}
```

#### 3. 与 SpringBoot 结合
```yaml
##application.yml
mybatis:
    type-aliases-package: com.huiyadan.crypt.mybatis.alias
    type-handlers-package: com.huiyadan.crypt.mybatis.type
```

注：以上配置方式**任选其一**即可，请根据实际情况选择。且当前的配置方法并不唯一，MyBatis 提供了很多配置方法，更多配置方法可以参考[官方文档](https://mybatis.org/mybatis-3/zh/configuration.html#typeHandlers)

### 修改 sqlmap 进行加密

写入时加密，查询时解密示例：

```xml
<!-- select： 在 resultMap 或 SQL 中需要加密的字段上声明 `javaType="encrypt"` -->
<resultMap id="BaseResultMap" type="user">
    <id column="id" property="id" jdbcType="BIGINT" />
    <result column="username" javaType="string" jdbcType="VARCHAR" property="username" />
    <result column="password" javaType="crypt" jdbcType="VARCHAR" property="password" />
</resultMap>

<!-- insert： 在 SQL 中需要加密的字段上声明 `javaType="crypt"` -->
<insert id="insert" parameterType="user">
    insert into user (id, username, password)
    values (#{id,jdbcType=BIGINT}, #{username,jdbcType=VARCHAR}, #{password, javaType=crypt, jdbcType=VARCHAR})
</insert>

<!-- update： 在 SQL 中需要加密的字段上声明 `javaType="crypt"` -->
<update id="update" parameterType="user">
    update user set password=#{password, javaType=crypt, jdbcType=VARCHAR} where id=#{id}
</update>
```

注：当前配置方式时比较简洁的方法，除了指定 `javaType` ，还可以通过指定 `typeHandler` 实现加解密。