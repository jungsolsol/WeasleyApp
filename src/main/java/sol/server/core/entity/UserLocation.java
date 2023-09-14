package sol.server.core.entity;

import jakarta.persistence.*;
import org.springframework.data.geo.Point;

@Entity
public class UserLocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long locationId;

    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;

    private String locationName;

    private Point locationPoint; // 장소 좌표


}
