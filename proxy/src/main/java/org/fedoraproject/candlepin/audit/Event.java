/**
 * Copyright (c) 2009 Red Hat, Inc.
 *
 * This software is licensed to you under the GNU General Public License,
 * version 2 (GPLv2). There is NO WARRANTY for this software, express or
 * implied, including the implied warranties of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. You should have received a copy of GPLv2
 * along with this software; if not, see
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt.
 *
 * Red Hat trademarks are not licensed under GPLv2. No permission is
 * granted to use or replicate Red Hat trademarks that are incorporated
 * in this software or its documentation.
 */
package org.fedoraproject.candlepin.audit;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.fedoraproject.candlepin.auth.Principal;
import org.fedoraproject.candlepin.model.Persisted;

/**
 * Event - Base class for Candlepin events.
 *
 * Servers as both our semi-permanent audit history in the database, as well as an
 * integral part of the event queue.
 */
@Entity
@Table(name = "cp_event")
@SequenceGenerator(name = "seq_event", sequenceName = "seq_event", allocationSize = 1)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "entitytype", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue("none")
public class Event implements Persisted {

    /**
     * Type - Constant representing the type of this event.
     */
    public enum EventType { ConsumerCreated, ConsumerModified, ConsumerUpdated };

    // Uniquely identifies the event:
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_event")
    private Long id;

    @Column(nullable = false)
    private EventType type;

    // String representation of the principal. We probably should not be reconstructing
    // any stored principal object.
    @Column(nullable = false)
    private String principal;

    @Column(nullable = false)
    private Date timestamp;

    // Uniquely identifies the entity's ID when combined with the event type.
    @Column(nullable = false)
    private Long entityId;

    // Both old/new may be null for creation/deletion events.
    private String oldEntity;
    private String newEntity;

    protected Event(EventType type, Principal principal,
        Long entityId, String oldEntity, String newEntity) {
        this.type = type;

        // TODO: toString good enough? Need something better?
        this.principal = principal.toString();

        this.entityId = entityId;
        this.oldEntity = oldEntity;
        this.newEntity = newEntity;

        // Set the timestamp to the current date and time.
        this.timestamp = new Date();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EventType getType() {
        return type;
    }

    public void setType(EventType type) {
        this.type = type;
    }

    public String getPrincipal() {
        return principal;
    }

    public void setPrincipal(String principal) {
        this.principal = principal;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public String getOldEntity() {
        return oldEntity;
    }
    public void setOldEntity(String oldEntity) {
        this.oldEntity = oldEntity;
    }
    public String getNewEntity() {
        return newEntity;
    }
    public void setNewEntity(String newEntity) {
        this.newEntity = newEntity;
    }

}