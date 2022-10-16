package agent;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import machine.EnigmaMachine;
import machine.Machine;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AgentTaskDeserializer implements JsonDeserializer<AgentTask> {

    @Override
    public AgentTask deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {

        JsonObject jsonObject = jsonElement.getAsJsonObject();

        // extract raw data
        String agentName = jsonObject.get("agentName").getAsString();
        String allieName = jsonObject.get("allieName").getAsString();
        int taskSize = jsonObject.get("taskSize").getAsInt();
        String textToDecipher = jsonObject.get("textToDecipher").getAsString();

        Type listIntegersType = new TypeToken<ArrayList<Integer>>() {
        }.getType();

        List<Integer> rotorsIDs = jsonDeserializationContext.deserialize(jsonObject.get("rotorsIDs"), listIntegersType);
        List<Integer> windowOffsets = jsonDeserializationContext.deserialize(jsonObject.get("windowOffsets"), listIntegersType);
        int inUseReflectorID = jsonObject.get("inUseReflectorID").getAsInt();

        Machine machine = jsonDeserializationContext.deserialize(jsonObject.get("machine"), EnigmaMachine.class);

        // build object manually

        return new AgentTask(rotorsIDs, windowOffsets, inUseReflectorID, machine, taskSize, textToDecipher, allieName);
    }
}
