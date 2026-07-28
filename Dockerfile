# Usamos una imagen oficial de Tomcat 10 (que soporta Jakarta nativamente) con JDK 17
FROM tomcat:10.1-jdk17

# Borramos la aplicación raíz por defecto de Tomcat
RUN rm -rf /usr/local/tomcat/webapps/ROOT

# Copiamos tu archivo WAR generado por Ant (asegúrate de que el nombre coincida con el tuyo, o usa ROOT.war)
COPY dist/Barstock.war /usr/local/tomcat/webapps/ROOT.war

# Exponemos el puerto 8080 para Railway
EXPOSE 8080

# Arrancamos Tomcat
CMD ["catalina.sh", "run"]