package team.oheck.commom;

public class ThreeItem<T, K, V> {
    private T first;
    private K second;
    private V third;

    public ThreeItem(T first, K second, V third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }

    public ThreeItem() {
    }

    public T getFirst() {
        return first;
    }

    public K getSecond() {
        return second;
    }

    public V getThird() {
        return third;
    }

    public void setFirst(T first) {
        this.first = first;
    }

    public void setSecond(K second) {
        this.second = second;
    }

    public void setThird(V third) {
        this.third = third;
    }

    @Override
    public int hashCode() {
        int var1 = 7;
        var1 = 31 * var1 + (this.first != null ? this.first.hashCode() : 0);
        var1 = 31 * var1 + (this.second != null ? this.second.hashCode() : 0);
        var1 = 31 * var1 + (this.third != null ? this.third.hashCode() : 0);
        return var1;
    }

    @Override
    public boolean equals(Object var1) {
        if (this == var1) {
            return true;
        } else if (!(var1 instanceof ThreeItem)) {
            return false;
        } else {
            ThreeItem var2 = (ThreeItem) var1;
            if (this.first != null) {
                if (!this.first.equals(var2.first)) {
                    return false;
                }
            } else if (var2.first != null) {
                return false;
            }

            if (this.second != null) {
                if (!this.second.equals(var2.second)) {
                    return false;
                }
            } else if (var2.second != null) {
                return false;
            }

            if (this.third != null) {
                if (!this.third.equals(var2.third)) {
                    return false;
                }
            } else if (var2.third != null) {
                return false;
            }

            return true;
        }
    }
}
