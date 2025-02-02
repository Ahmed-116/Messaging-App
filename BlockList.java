public class BlockList extends Contact {
    public BlockList(String id, String name, String phoneNumber) {
        super(id, name, phoneNumber);
    }

    @Override
    public String toString() {
        return "Blocked Contact - Name: " + getName() + " | Number: " + getPhoneNumber();
    }
}
