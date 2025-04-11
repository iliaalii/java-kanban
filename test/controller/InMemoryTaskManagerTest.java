package controller;

import org.junit.jupiter.api.BeforeEach;

class InMemoryTaskManagerTest extends TaskManagerTest {

    @BeforeEach
    void beforeEach() {
        manager = new InMemoryTaskManager();
        super.beforeEach();
    }
}