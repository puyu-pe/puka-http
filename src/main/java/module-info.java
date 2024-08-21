module pe.puyu.pukahttp {
	requires transitive javafx.graphics;
	requires javafx.controls;
	requires javafx.fxml;

	requires java.desktop;

	requires ch.qos.logback.classic;
	requires ch.qos.logback.core;
	requires org.slf4j;

	requires com.google.gson;
    opens pe.puyu.pukahttp.domain.models to com.google.gson;
    opens pe.puyu.pukahttp.infrastructure.javalin.models to com.google.gson;

	requires net.harawata.appdirs;

	requires escpos.coffee;
	requires com.fazecast.jSerialComm;
	requires pe.puyu.jticketdesing;
	requires org.jetbrains.annotations;

	requires io.javalin;
	requires java.net.http;
	requires java.sql;
	requires com.dustinredmond.fxtrayicon;
    requires jcommander;
	requires jdk.jdi;
	requires pe.puyu.SweetTicketDesign;

	opens pe.puyu.pukahttp.infrastructure.javafx.controllers to javafx.fxml, javafx.graphics;
	opens pe.puyu.pukahttp.infrastructure.javafx.app to javafx.fxml, javafx.graphics;

}
