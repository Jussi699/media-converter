package model.utility;

import javafx.scene.control.ComboBox;
import ws.schild.jave.info.MultimediaInfo;

public class Parsers {
    public static int parseComboBoxStringToInt(ComboBox<String> cb) {
        return Integer.parseInt(cb.getValue().replaceAll("[^0-9]", ""));
    }

    public static int parseChannels(MultimediaInfo info) {
        return (info != null && info.getAudio() != null) ? info.getAudio().getChannels() : -1;
    }

    public static int parseSamplingRate(MultimediaInfo info) {
        return (info != null && info.getAudio() != null) ? info.getAudio().getSamplingRate() : -1;
    }

    public static int parseBitrate(MultimediaInfo info) {
        if (info == null) return -1;
        if (info.getVideo() != null && info.getVideo().getBitRate() > 0) return info.getVideo().getBitRate() / 1000;
        if (info.getAudio() != null && info.getAudio().getBitRate() > 0) return info.getAudio().getBitRate() / 1000;
        return -1;
    }

    public static int parseVideoBitrate(MultimediaInfo info) {
        if (info != null && info.getVideo() != null && info.getVideo().getBitRate() > 0) {
            return info.getVideo().getBitRate() / 1000;
        }
        return -1;
    }

    public static int parseAudioBitrate(MultimediaInfo info) {
        if (info != null && info.getAudio() != null && info.getAudio().getBitRate() > 0) {
            return info.getAudio().getBitRate() / 1000;
        }
        return -1;
    }

    public static String parseResolution(MultimediaInfo info) {
        if (info != null && info.getVideo() != null && info.getVideo().getSize() != null) {
            return info.getVideo().getSize().getWidth() + "x" + info.getVideo().getSize().getHeight();
        }
        return null;
    }

    public static int parseFps(MultimediaInfo info) {
        return (info != null && info.getVideo() != null) ? (int) info.getVideo().getFrameRate() : -1;
    }
}
