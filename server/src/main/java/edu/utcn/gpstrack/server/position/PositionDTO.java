package edu.utcn.gpstrack.server.position;

import lombok.Data;

import java.util.Date;

/**
 * @author Radu Miron
 * @version 1
 */
@Data
public class PositionDTO {
    private Integer id;

    private String terminalId;
    private String latitude;
    private String longitude;
    private Date creationDate;
}
