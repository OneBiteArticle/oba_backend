package oba.backend.server.repository.mysql;

import oba.backend.server.entity.mysql.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Integer> {}
