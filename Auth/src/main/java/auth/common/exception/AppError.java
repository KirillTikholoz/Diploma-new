package auth.common.exception;

import lombok.Data;

import java.util.Date;

@Data
public class AppError {
    private int status;
    private String messsage;
    private Date timestamp;

    public AppError(int status, String messsage){
        this.status = status;
        this.messsage = messsage;
        this.timestamp = new Date();
    }

}
