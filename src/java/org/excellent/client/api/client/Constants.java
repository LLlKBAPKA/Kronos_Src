package org.excellent.client.api.client;

import lombok.experimental.UtilityClass;

import java.nio.file.Path;
import java.nio.file.Paths;

@UtilityClass
public class Constants {
    public final String NAME = "Excellent";
    public final String NAMESPACE = NAME.toLowerCase();
    public final String RELEASE = "Recode";
    public final String VERSION = "291124";
    public final String WEBSITE_SHORT = "excellentclient.pw";
    public final String WEBSITE = "https://" + WEBSITE_SHORT;
    public final String TITLE = String.format("%s %s (%s) -> %s", NAME, VERSION, RELEASE, WEBSITE);
    //public final Path MAIN_DIR = Paths.get("C:/ExcellentRecode/game");
    public final Path MAIN_DIR = Paths.get("E:/ERROR422/SRC/Exc/workspace");
    public final String FILE_FORMAT = ".exc";
    public final String DEVELOPER = "sheluvparis";
}
