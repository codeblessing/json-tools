package pl.poznan.put.jsontools.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import pl.poznan.put.jsontools.logic.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Validated
@RestController
@RequestMapping("/api")
public class JsonToolsController {
    private static final Logger _logger = LoggerFactory.getLogger(JsonToolsController.class);

    private final JsonTransformService transformService;

    public JsonToolsController(JsonTransformService transformService) {
        this.transformService = transformService;
    }

    @RequestMapping(method = RequestMethod.GET, produces = "application/json")
    public String get(@Valid @RequestBody JsonToolsFullRequest request) {
        _logger.debug("Got request:\n" + request.toString());
        _logger.info("Processing transforms.");

        List<String> altered = new ArrayList<>();
        for (var json : request.data) {
            JsonTransform transform = new JsonBase(json.toString());
            JsonTransform baseTransform = transform;
            for (var tform : request.transforms) {
                switch (tform.name) {
                    case "remove-attributes":
                        _logger.debug("Remove transform added");
                        transform = new JsonTransformRemoveAttributes(baseTransform, tform.attributes);
                        baseTransform = transform;
                        break;
                    case "retain-attributes":
                        _logger.debug("Retain transform added");
                        transform = new JsonTransformRetainAttributes(baseTransform, tform.attributes);
                        baseTransform = transform;
                        break;
                    case "minify":
                        _logger.debug("Minify transform added");
                        transform = new JsonTransformMinify(baseTransform);
                        baseTransform = transform;
                        break;
                    case "format":
                        _logger.debug("Format transform added");
                        transform = new JsonTransformFormat(baseTransform);
                        baseTransform = transform;

                    case "flatten":
                        _logger.debug("Flatten transform added");
                        transform = new JsonTransformFlatten(baseTransform);
                        baseTransform = transform;
                        break;
                    case "sort":
                        _logger.debug("Sort transform added");
                        transform = new JsonTransformSortFields(baseTransform);
                        baseTransform = transform;
                        break;
                    case "count":
                        _logger.debug("Count transform added");
                        transform = new JsonTransformCountFieldsValues(baseTransform);
                        baseTransform = transform;
                        break;
                    case "delete-nulls":
                        _logger.debug("DeleteNulls transform added");
                        transform = new JsonTransformDeleteNulls(baseTransform);
                        baseTransform = transform;
                        break;
                    default:
                        _logger.warn("No such transform: " + tform.name);
                        break;
                }

            }
            altered.add(transform.execute());
        }


        if (altered.size() == 1) {
            return altered.get(0);
        } else {
            return "[\n\t" + altered.stream()
                    .map(str -> str.replace("\n", "\n\t"))
                    .collect(Collectors.joining(",\n\t")) + "\n]";
        }

        return transformService.transform(request);
    }


    @RequestMapping(value = "/remove-attributes", method = RequestMethod.GET, produces = "application/json")
    public String removeAttributes(@Validated @RequestBody JsonToolsSingleRequest request) {
        return transformService.removeAttributes(request);
    }


    @RequestMapping(value = "/retain-attributes", method = RequestMethod.GET, produces = "application/json")
    public String retainAttributes(@Validated @RequestBody JsonToolsSingleRequest request) {
        return transformService.retainAttributes(request);
    }


    @RequestMapping(value = "/flatten", method = RequestMethod.GET, produces = "application/json")
    public String flatten(@Validated @RequestBody JsonToolsSingleRequest request) {
        return transformService.flatten(request);
    }


    @RequestMapping(value = "/minify", method = RequestMethod.GET, produces = "application/json")
    public String minify(@Validated @RequestBody JsonToolsSingleRequest request) {
        return transformService.minify(request);
    }


    @RequestMapping(value = "/format", method = RequestMethod.GET, produces = "application/json")
    public String format(@Validated @RequestBody JsonToolsSingleRequest request) {
        return transformService.format(request);
    }


    @RequestMapping(value = "/sort", method = RequestMethod.GET, produces = "application/json")
    public String sortAttributes(@Validated @RequestBody JsonToolsSingleRequest request) {
        return transformService.sortAttributes(request);
    }


    @RequestMapping(value = "/count", method = RequestMethod.GET, produces = "application/json")
    public String countFieldsValues(@Validated @RequestBody JsonToolsSingleRequest request) {
        return transformService.countFieldsValues(request);
    }

    @RequestMapping(value = "/delete-nulls", method = RequestMethod.GET, produces = "application/json")
    public String deleteNulls(@Validated @RequestBody JsonToolsSingleRequest request) {
        var transform = new JsonTransformDeleteNulls(new JsonBase(request.data.toString()));
        return transform.execute();
    }
  
}


