package odtwarzacz;

public enum FileType {
    NONE, AUDIO, VIDEO;

    @Override
    public String toString() {
        switch (this) {
            case AUDIO:
                return "Audio";

            case VIDEO:
                return "Video";

                default:
                    return "None";
        }
    }
}
