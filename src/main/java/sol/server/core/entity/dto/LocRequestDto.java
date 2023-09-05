package sol.server.core.entity.dto;

import lombok.Data;
import lombok.NonNull;

@Data
public class LocRequestDto {
    @NonNull
    private String latitude;
    @NonNull
    private String longitude;
    private String locationName;
//    @User로 분리
//    private String userKey; /**사용자키**/
//    private String authKey; /**제품키**/


}
