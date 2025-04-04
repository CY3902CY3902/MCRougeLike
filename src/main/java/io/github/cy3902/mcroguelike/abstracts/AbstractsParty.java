package io.github.cy3902.mcroguelike.abstracts;

import java.util.ArrayList;
import java.util.List;

public class AbstractsParty {
    protected String name;
    protected List<String> members = new ArrayList<>();
    protected String leader;

    public AbstractsParty(String name) {
        this.name = name;
    }

    public void addMember(String member) {
        members.add(member);
    }

    public void removeMember(String member) {
        members.remove(member);
    }

    public String getName() {
        return name;
    }

    public List<String> getMembers() {
        return members;
    }
    public void setLeader(String leader) {
        this.leader = leader;
    }

}
