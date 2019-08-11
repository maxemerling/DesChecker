class Item {
    private String number, name, description;

    Item(String number, String name, String description) {
        this.number = number;
        this.name = name;
        this.description = description;
    }

    String getNumber() {
        return number;
    }

    String getName() {
        return name;
    }

    String getDescription() {
        return description;
    }
}
