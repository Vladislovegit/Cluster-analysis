package dip;

public class Coordinates<K, V, C, D> {

    private K first;
    private V second;
    private C third;
    private D fourth;

    public Coordinates(K k, V v, C c, D d) {
        first = k;
        second = v;
        third = c;
        fourth = d;
    }

    public void setFirst(K first) {
        this.first = first;
    }

    public void setSecond(V second) {
        this.second = second;
    }

    public void setThird(C third) {
        this.third = third;
    }

    public void setFourth(D fourth) {
        this.fourth = fourth;
    }

    public K getFirst() {
        return first;
    }

    public V getSecond() {
        return second;
    }

    public C getThird() {
        return third;
    }

    public D getFourth() {
        return fourth;
    }
}
