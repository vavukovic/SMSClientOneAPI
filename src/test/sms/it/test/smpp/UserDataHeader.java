package sms.it.test.smpp;


import java.util.Arrays;

/**
 * @author mstipanov
 * @since 07.06.11. 08:40
 */
public class UserDataHeader {
    private byte userDataHeaderLength;
    private byte informationElementIdentifier;
    private byte headerLength;
    private byte[] ref;
    private byte count;
    private byte index;

    public UserDataHeader() {
    }

    public UserDataHeader(byte userDataHeaderLength, byte informationElementIdentifier, byte headerLength, byte[] ref, byte count, byte index) {
        this.userDataHeaderLength = userDataHeaderLength;
        this.informationElementIdentifier = informationElementIdentifier;
        this.headerLength = headerLength;
        this.ref = ref;
        this.count = count;
        this.index = index;
    }

    public UserDataHeader(byte[] ref, byte count, byte index) {
        this.ref = ref;
        this.count = count;
        this.index = index;
    }

    public byte getUserDataHeaderLength() {
        return userDataHeaderLength;
    }

    public void setUserDataHeaderLength(byte userDataHeaderLength) {
        this.userDataHeaderLength = userDataHeaderLength;
    }

    public byte getInformationElementIdentifier() {
        return informationElementIdentifier;
    }

    public void setInformationElementIdentifier(byte informationElementIdentifier) {
        this.informationElementIdentifier = informationElementIdentifier;
    }

    public byte getHeaderLength() {
        return headerLength;
    }

    public void setHeaderLength(byte headerLength) {
        this.headerLength = headerLength;
    }

    public byte[] getRef() {
        return ref;
    }

    public void setRef(byte[] ref) {
        this.ref = ref;
    }

    public byte getCount() {
        return count;
    }

    public void setCount(byte count) {
        this.count = count;
    }

    public byte getIndex() {
        return index;
    }

    public void setIndex(byte index) {
        this.index = index;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        UserDataHeader that = (UserDataHeader) o;

        if (count != that.count) {
            return false;
        }
        if (headerLength != that.headerLength) {
            return false;
        }
        if (index != that.index) {
            return false;
        }
        if (informationElementIdentifier != that.informationElementIdentifier) {
            return false;
        }
        if (userDataHeaderLength != that.userDataHeaderLength) {
            return false;
        }

        if (checkRef(that)) {
            return false;
        }

        return true;
    }

    /**
     * this ensures thar ref checking is ignored if ref is null
     * It is used when IpCore solits the message and we don't know the reference in advance
     */
    private boolean checkRef(UserDataHeader that) {
        return null != ref && !Arrays.equals(ref, that.ref);
    }

    @Override
    public int hashCode() {
        int result = (int) userDataHeaderLength;
        result = 31 * result + (int) informationElementIdentifier;
        result = 31 * result + (int) headerLength;
        result = 31 * result + (ref != null ? Arrays.hashCode(ref) : 0);
        result = 31 * result + (int) count;
        result = 31 * result + (int) index;
        return result;
    }

    @Override
    public String toString() {
        return "UserDataHeader{" +
                "userDataHeaderLength=" + userDataHeaderLength +
                ", informationElementIdentifier=" + informationElementIdentifier +
                ", headerLength=" + headerLength +
                ", ref=" + Arrays.toString(ref) +
                ", count=" + count +
                ", index=" + index +
                '}';
    }
}
