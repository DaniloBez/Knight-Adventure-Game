package Assembly.Enjoyers.Utils;

public class TimeConverter {
    /**
     * Форматує час у вигляд mm:ss.
     * @param timeInSeconds час у секундах.
     * @return стрічка у вигляді mm:ss.
     */
    public static String formatTime(float timeInSeconds) {
        int totalSeconds = (int) timeInSeconds;
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
}
