![](https://github.com/qqdog1/simpleConnect/workflows/Simple%20Connect%20build/badge.svg)

# SimpleConnect
# 安裝

## 1. 修改POM
增加Maven Repo

    <repositories>
        <repository>
            <id>github</id>
            <url>https://maven.pkg.github.com/qqdog1/simple-connect</url>
        </repository>
    </repositories>

Dependency

Server

    <dependency>
    <groupId>name.qd.simpleConnect</groupId>
        <artifactId>server</artifactId>
        <version>1.0.3</version>
    </dependency>
    <dependency>
        <groupId>name.qd.simpleConnect</groupId>
        <artifactId>common</artifactId>
        <version>1.0.3</version>
    </dependency>
    
Client

    <dependency>
        <groupId>name.qd.simpleConnect</groupId>
        <artifactId>client</artifactId>
        <version>1.0.3</version>
    </dependency>
    <dependency>
        <groupId>name.qd.simpleConnect</groupId>
        <artifactId>common</artifactId>
        <version>1.0.3</version>
    </dependency>

## 2. 修改 settings.xml
在~/.m2/settings.xml中增加設定  

    <servers>
	    <server>
		    <id>github</id>
		    <username>your own github user name</username>
		    <password>your own github token</password>
	    </server>
    </servers>
    
token相關設定請參考Github說明文件[Configuring Apache Maven for use with GitHub Packages](https://help.github.com/en/github/managing-packages-with-github-packages/configuring-apache-maven-for-use-with-github-packages#authenticating-to-github-packages)

# 使用方式
參考WIKI頁  
https://github.com/qqdog1/simple-connect/wiki/%E4%BD%BF%E7%94%A8%E6%96%B9%E5%BC%8F
