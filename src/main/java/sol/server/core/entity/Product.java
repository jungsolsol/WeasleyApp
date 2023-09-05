package sol.server.core.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productKey;

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    private List<User> user = new ArrayList<>();



}
