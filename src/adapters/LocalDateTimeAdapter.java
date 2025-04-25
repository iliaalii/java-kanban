package adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Objects;

public class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {

    @Override
    public void write(JsonWriter jsonWriter, LocalDateTime localDateTime) throws IOException {
        jsonWriter.value(String.valueOf((localDateTime)));
    }

    @Override
    public LocalDateTime read(JsonReader jsonReader) throws IOException {
        String str = jsonReader.nextString();
        if (Objects.equals(str, "null")) {
            return null;
        }
        return LocalDateTime.parse(str);
    }
}
