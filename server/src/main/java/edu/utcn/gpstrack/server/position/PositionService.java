package edu.utcn.gpstrack.server.position;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Radu Miron
 * @version 1
 */
@Service
public class PositionService {

    private final PositionRepository positionRepository;

    public PositionService(PositionRepository positionRepository) {
        this.positionRepository = positionRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public PositionDTO create(PositionDTO position) {
        Position positionEntity = this.convertToEntity(position);

        return this.convertToDTO(positionRepository.save(positionEntity));
    }

    @Transactional
    public PositionDTO read(Integer id) {
        return this.convertToDTO(positionRepository.findById(id).orElse(null));
    }

    @Transactional
    public List<PositionDTO> readAll(Date startDate, Date endDate) {
        if (!startDate.toString().isEmpty() && !endDate.toString().isEmpty()) {
            return positionRepository.findAllBetweenDates(startDate, endDate).stream().map(this::convertToDTO).collect(Collectors.toList());
        } else {
            return positionRepository.findAll().stream().map(this::convertToDTO).collect(Collectors.toList());
        }
    }

    @Transactional
    public PositionDTO update(Integer id, PositionDTO position) {
        Position requestedPosition = positionRepository.findById(id).orElse(null);
        requestedPosition.setLatitude(position.getLatitude());
        requestedPosition.setLongitude(position.getLongitude());
        requestedPosition.setTerminalId(position.getTerminalId());

        return this.convertToDTO(positionRepository.save(requestedPosition));
    }

    @Transactional
    public PositionDTO delete(Integer id) {
        Position requestedPosition = positionRepository.findById(id).orElse(null);
        positionRepository.delete(requestedPosition);

        return this.convertToDTO(requestedPosition);
    }

    private PositionDTO convertToDTO(Position position) {
        ModelMapper modelMapper = new ModelMapper();
        PositionDTO positionDTO = modelMapper.map(position, PositionDTO.class);

        return positionDTO;
    }

    private Position convertToEntity(PositionDTO positionDTO) {
        ModelMapper modelMapper = new ModelMapper();
        Position position = modelMapper.map(positionDTO, Position.class);

        return position;
    }
}
