package sol.server.core.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity(name = "Users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    private String userName;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Product product;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<UserLocation> mainLocations = new ArrayList<>(); // 주요 장소 목록, 최대 5개

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<UserLocation> subLocations = new ArrayList<>(); // 서브 장소 목록, 최대 5개


    public void addMainLocation(UserLocation userLocation) {
        if (mainLocations.size() + subLocations.size() < 5) {
            mainLocations.add(userLocation);
        } else {
            throw new IllegalStateException("장소 등록은 최대 5개");
        }
    }

    public void addSubLocation(UserLocation userLocation) {
        if (mainLocations.size() + subLocations.size() < 5) {
            subLocations.add(userLocation);
        } else {
            throw new IllegalStateException("장소 등록은 최대 5개");
        }
    }
}



