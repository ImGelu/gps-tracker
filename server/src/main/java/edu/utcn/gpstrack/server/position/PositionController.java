package edu.utcn.gpstrack.server.position;

import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * @author Radu Miron
 * @version 1
 */
@RestController
@RequestMapping("/positions")
public class PositionController {

    private final PositionService positionService;

    public PositionController(PositionService positionService) {
        this.positionService = positionService;
    }

    @PostMapping
    public PositionDTO create(@RequestBody PositionDTO position) {
        return positionService.create(position);
    }

    @GetMapping
    public List<PositionDTO> readAll(@RequestParam(name = "startDate", required = false, defaultValue = "1000-01-01") String startDate, @RequestParam(name = "endDate", required = false, defaultValue = "9999-01-01") String endDate) throws ParseException {
        return positionService.readAll(new SimpleDateFormat("yyyy-MM-dd").parse(startDate), new SimpleDateFormat("yyyy-MM-dd").parse(endDate));
    }

    @GetMapping("/{id}")
    public PositionDTO read(@PathVariable Integer id) {
        return positionService.read(id);
    }

    @PutMapping("/{id}")
    public PositionDTO update(@PathVariable Integer id, @RequestBody PositionDTO position) {
        return positionService.update(id, position);
    }

    @DeleteMapping("/{id}")
    public PositionDTO delete(@PathVariable Integer id) {
        return positionService.delete(id);
    }

}
