FROM tomcat:8
ADD target/auth-course-0.0.1-SNAPSHOT.jar auth-course-0.0.1-SNAPSHOT.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "auth-course-0.0.1-SNAPSHOT.jar"]