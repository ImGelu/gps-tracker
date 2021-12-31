package edu.utcn.gpstrack.server.position;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * @author Radu Miron
 * @version 1
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/positions")
public class PositionController {

    @Autowired
    PositionService positionService;

    @PostMapping
    public PositionDTO create(@RequestBody PositionDTO position) {
        return positionService.create(position);
    }

    @GetMapping
    public List<PositionDTO> readAll(@RequestParam(name = "startDate", required = false, defaultValue = "1000-01-01") String startDate, @RequestParam(name = "endDate", required = false, defaultValue = "9999-01-01") String endDate, @RequestParam(name = "terminalId", required = false) String terminalId) throws ParseException {
        return positionService.readAll(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(startDate), new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(endDate), terminalId);
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
