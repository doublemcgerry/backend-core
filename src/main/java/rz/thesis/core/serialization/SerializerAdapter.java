package rz.thesis.core.serialization;
import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * This generic class will define the custom serializer for json serialization,
 * enabling the deserialization into the subclasses of an abstract class
 * 
 * @author Andrea
 *
 * @param <T> Abstract class of which there is the need to create a custom serializer
 */
public class SerializerAdapter<T> implements JsonSerializer<T>, JsonDeserializer<T> {

    private static final String CLASSNAME = "CLASSNAME";
    private static final String SIMPLENAME = "SIMPLENAME";
    private static final String INSTANCE = "INSTANCE";

    @Override
    public JsonElement serialize(T src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject retValue = new JsonObject();
        String className = src.getClass().getCanonicalName();
        retValue.addProperty(CLASSNAME, className);
        retValue.addProperty(SIMPLENAME, src.getClass().getSimpleName());
        JsonElement elem = context.serialize(src);
        retValue.add(INSTANCE, elem);
        return retValue;
    }

    @Override
    public T deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
             {
        JsonObject jsonObject = json.getAsJsonObject();
        JsonPrimitive prim = (JsonPrimitive) jsonObject.get(CLASSNAME);
        String className = prim.getAsString();

        Class<?> klass = null;
        try {
            klass = Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new JsonParseException(e.getMessage(),e);
        }
        return context.deserialize(jsonObject.get(INSTANCE), klass);
    }
}