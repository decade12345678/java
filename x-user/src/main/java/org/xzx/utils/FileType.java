package org.xzx.utils;

public enum FileType {
    IMAGE(new String[]{"jpg", "jpeg", "png", "gif", "bmp"}, "[图片]"),
    VIDEO(new String[]{"mp4", "avi", "mov", "wmv"}, "[视频]"),
    AUDIO(new String[]{"mp3", "wav", "aac"}, "[音频]"),
    DOCUMENT(new String[]{"pdf", "doc", "docx"}, "[文档]"),
    DEFAULT(new String[]{}, "[文件]");
    
    private final String[] extensions;
    private final String displayName;
    
    FileType(String[] extensions, String displayName) {
        this.extensions = extensions;
        this.displayName = displayName;
    }
    
    public static String getDisplayName(String extension) {
        if (extension == null) return DEFAULT.displayName;
        String ext = extension.toLowerCase();
        for (FileType type : values()) {
            for (String e : type.extensions) {
                if (e.equals(ext)) {
                    return type.displayName;
                }
            }
        }
        return DEFAULT.displayName;
    }
}