package ru.geekbrains.qa.java.project;

    public final class ApplicationGlobalState {

        private static ApplicationGlobalState INSTANCE;
        private String selectedCity = null;
        private final String API_KEY = "ZnKd4kTYc68bc36GD8GEqWoC70D6IGMB";

        private ApplicationGlobalState() {
        }

        // Непотокобезопасный код для упрощения
        public static ApplicationGlobalState getInstance() {
            if(INSTANCE == null) {
                INSTANCE = new ApplicationGlobalState();
            }

            return INSTANCE;
        }

        public String getSelectedCity() {
            return selectedCity;
        }

        public void setSelectedCity(String selectedCity) {
            this.selectedCity = selectedCity;
        }

        public String getApiKey() {
            return this.API_KEY;
        }
}
