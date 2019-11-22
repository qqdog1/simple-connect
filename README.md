![](https://github.com/qqdog1/simpleConnect/workflows/Simple%20Connect%20build/badge.svg)

# SimpleConnect

Maven Repo

    <repositories>
        <repository>
            <id>SimpleConnect-mvn-repo</id>
            <url>https://raw.github.com/qqdog1/SimpleConnect/mvn-repo/</url>
            <snapshots>
                <enabled>true</enabled>
                <updatePolicy>always</updatePolicy>
            </snapshots>
        </repository>
    </repositories>

Dependency

Server

    <dependency>
    <groupId>name.qd.simpleConnect</groupId>
        <artifactId>server</artifactId>
        <version>1.0</version>
    </dependency>
    <dependency>
        <groupId>name.qd.simpleConnect</groupId>
        <artifactId>common</artifactId>
        <version>1.0</version>
    </dependency>
    
Client

    <dependency>
        <groupId>name.qd.simpleConnect</groupId>
        <artifactId>client</artifactId>
        <version>1.0</version>
    </dependency>
    <dependency>
        <groupId>name.qd.simpleConnect</groupId>
        <artifactId>common</artifactId>
        <version>1.0</version>
    </dependency>
