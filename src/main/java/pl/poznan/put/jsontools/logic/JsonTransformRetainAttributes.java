package pl.poznan.put.jsontools.logic;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.poznan.put.jsontools.error.JsonToolsInvalidJsonError;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class JsonTransformRetainAttributes extends JsonTransformer{

    /**
     * Attributes to save from JSON.
     */
    private final List<String> _attributes;
    /**
     * Class-level logger instance.
     */
    private static final Logger _logger = LoggerFactory.getLogger(JsonTransformRetainAttributes.class);
    

    /**
     * @param transform transform to be executed before attributes will be save.
     * @param attributes attributes to be saved from JSON.
     */
    public JsonTransformRetainAttributes(JsonTransform transform, List<String> attributes) {
        super(transform);
        this._attributes = attributes;
        _logger.debug("RetainAttributes transform created.");
    }

    /**
     * It retains given attributes.
     * @return  JSON containing only attributes specified by  attributes
     */
    @Override
    public String execute() {
        String json = super.execute();
        _logger.debug("Executing saving attributes");
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(json);
            List<String> jsonAttributes = new ArrayList<>();
            Iterator<String> NameIterator = jsonNode.fieldNames();
            while(NameIterator.hasNext()) {
                jsonAttributes.add(NameIterator.next());
            }
            for(String attribute: jsonAttributes){
                if(!this._attributes.contains(attribute)){
                    ((ObjectNode)jsonNode).remove(attribute);
                }
            }

            return mapper.convertValue(jsonNode, JsonNode.class).toString();
        }
        catch (JsonProcessingException e) {
            throw new JsonToolsInvalidJsonError("Invalid JSON", json);
        }
    }
}
