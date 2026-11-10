
// Cách tự viết 1 class immutable: `final class`, field `private final`, không setter, defensive copy cho List/Date.

public final class ImmutableStudent {

    private final String name;
    private final Date dateOfBirth;
    private final List<String> courses;

    ImmutableStudent(String name, Date dateOfBirth, List<String> courses){
        this.name = name;
        this.DOB = new Date(DOB.getTime());
        this.course = new ArrayList<>(course)
    }

    public String getname(){
        return name;
    }
    public Date getDateOfBirth() {
        return new Date(dateOfBirth.getTime());
    }

    public List<String> getCourses() {
        // 6. Trả về Unmodifiable List hoặc copy
        // Cách 1: return new ArrayList<>(this.courses);
        // Cách 2: Trả về unmodifiable view (khuyên dùng vì tối ưu bộ nhớ hơn)
        return Collections.unmodifiableList(courses);
    }




}
public static void main(String[] args){
    List<String> courses = new ArrayList<>() ;
    Date dateOfBirth = new Date();
    ImmutableStudent Imst = new ImmutableStudent("Tien",dateOfBirth, courses)
}