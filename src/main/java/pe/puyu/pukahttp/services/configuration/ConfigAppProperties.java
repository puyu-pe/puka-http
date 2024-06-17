package pe.puyu.pukahttp.services.configuration;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;
import pe.puyu.pukahttp.Constants;
import pe.puyu.pukahttp.util.AppProperties;
import pe.puyu.pukahttp.util.AppUtil;

import java.util.Optional;

public class ConfigAppProperties {
	private AppProperties properties = null;
	private final Logger logger = (Logger) LoggerFactory.getLogger(AppUtil.makeNamespaceLogs("ConfigAppProperties"));

	public ConfigAppProperties() {
		try {
			properties = new AppProperties(AppUtil.getConfigAppFileDir());
		} catch (Exception e) {
			logger.error("Fatal error on load config.ini {}", e.getMessage(), e);
		}
	}

	public Optional<Boolean> uniqueProcess() {
		try {
			var uniqueProcess = Optional.ofNullable(properties.get("uniqueProcess"));
			return uniqueProcess.map(s -> s.equalsIgnoreCase("si"));
		} catch (Exception e) {
			logger.error("Exception on get uniqueProcess configuration: {}", e.getMessage(), e);
			return Optional.empty();
		}
	}

	public void uniqueProcess(boolean value) {
		try {
			var configValue = value ? "si" : "no";
			properties.set("uniqueProcess", configValue);
		} catch (Exception e) {
			logger.error("Exception on set uniqueProcess: {}", e.getMessage(), e);
		}
	}

	public void notifications(boolean value) {
		try {
			var configValue = value ? "si" : "no";
			properties.set("notificationEnabled", configValue);
		} catch (Exception e) {
			logger.error("Exception on set notificationEnabled: {}", e.getMessage(), e);
		}
	}

	public Optional<Boolean> notifications() {
		try {
			return Optional.of(properties.get("notificationEnabled").equalsIgnoreCase("si"));
		} catch (Exception e) {
			logger.error("Exception on get notificationEnabled configuration: {}", e.getMessage(), e);
			return Optional.empty();
		}
	}

	public void rootLoggerLevel(Level loggerLevel){
		try{
			properties.set("rootLoggerLevel", loggerLevel.levelStr);
		}catch(Exception e){
			logger.error("Exception on get rootLoggerLevel: {}", e.getMessage(), e);
		}
	}

	public Optional<Level> rootLoggerLevel(){
		try{
			Optional<String> levelStr = Optional.ofNullable(properties.get("rootLoggerLevel"));
			return levelStr.flatMap(s -> Optional.of(s).map(Level::toLevel));
		}catch(Exception e){
			return Optional.empty();
		}
	}

	public void printDelay(int delay){
		try{
			int printDelayMax = 1000, printDelayMin = 0;
			delay = Math.min(printDelayMax , Math.max(printDelayMin, delay));
			properties.set("printDelay", String.valueOf(delay));
		}catch (Exception e){
			logger.error("Exception on get printDelay: {}", e.getMessage(), e);
		}
	}

	public Optional<Integer> printDelay(){
		try{
			Optional<String> printDelayStr = Optional.ofNullable(properties.get("printDelay"));
			int printDelay;
			try{
				printDelay = Integer.parseInt(printDelayStr.orElse(Constants.printDelayDefaultStr));
			}catch (Exception ignored){
				printDelay = Constants.printDelayDefault;
			}
			int printDelayMax = 1000, printDelayMin = 0;
			printDelay = Math.min(printDelayMax , Math.max(printDelayMin, printDelay));
			return Optional.of(printDelay);
		}catch (Exception e){
			return Optional.empty();
		}
	}

}
