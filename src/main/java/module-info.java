module pe.puyu.pukahttp {
	requires transitive javafx.graphics;
	requires javafx.controls;
	requires javafx.fxml;

	requires java.desktop;

	requires ch.qos.logback.classic;
	requires ch.qos.logback.core;
	requires org.slf4j;

	requires com.google.gson;
	requires org.hildan.fxgson;
	opens pe.puyu.pukahttp.model to com.google.gson, org.hildan.fxgson;
	opens pe.puyu.pukahttp.services.api to com.google.gson, org.hildan.fxgson;

	requires net.harawata.appdirs;

	requires escpos.coffee;
	requires com.fazecast.jSerialComm;
	requires pe.puyu.jticketdesing;
	requires org.jetbrains.annotations;

	requires io.javalin;
	requires java.net.http;
	requires tyrus.standalone.client;
	requires ormlite.jdbc;
	requires java.sql;
	requires com.h2database;
	requires com.dustinredmond.fxtrayicon;
	opens pe.puyu.pukahttp.repository.model to ormlite.jdbc;

//	opens pe.puyu.pukahttp.views to javafx.fxml, javafx.graphics;
//	opens pe.puyu.pukahttp.app to javafx.fxml, javafx.graphics;
	opens pe.puyu.pukahttp.infrastructure.javafx.controllers to javafx.fxml, javafx.graphics;
	opens pe.puyu.pukahttp.infrastructure.javafx.app to javafx.fxml, javafx.graphics;

	exports pe.puyu.pukahttp.app;
	exports pe.puyu.pukahttp.repository.model;
	exports pe.puyu.pukahttp.util;
	exports pe.puyu.pukahttp.model;
	exports pe.puyu.pukahttp.services.api;
	exports pe.puyu.pukahttp.repository;
}
