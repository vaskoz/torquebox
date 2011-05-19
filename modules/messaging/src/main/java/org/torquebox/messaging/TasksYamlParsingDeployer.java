/*
 * Copyright 2008-2011 Red Hat, Inc, and individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 * 
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.torquebox.messaging;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.jboss.as.server.deployment.DeploymentUnit;
import org.torquebox.core.AbstractSplitYamlParsingProcessor;
import org.yaml.snakeyaml.Yaml;

/**
 * <pre>
 * Stage: PARSE
 *    In: messaging.yml
 *   Out: TaskMetaData
 * </pre>
 * 
 * Creates TaskMetaData instances from messaging.yml
 */
public class TasksYamlParsingDeployer extends AbstractSplitYamlParsingProcessor {

    public TasksYamlParsingDeployer() {
        setSectionName( "tasks" );
    }

    @Override
    public void parse(DeploymentUnit unit, Object dataObj) throws Exception {
        for (TaskMetaData metaData : Parser.parse( dataObj, unit.getAttachmentList( TaskMetaData.ATTACHMENT_KEY ) ) ) {
            unit.addToAttachmentList( TaskMetaData.ATTACHMENT_KEY, metaData );
        }
    }

    public static class Parser {

        @SuppressWarnings({ "unchecked", "rawtypes" })
        static List<TaskMetaData> parse(Object data, List<? extends TaskMetaData> existingTasks) throws Exception {
            List<TaskMetaData> result = null;

            if (data instanceof String) {
                String s = (String) data;
                if (s.trim().length() == 0) {
                    result = Collections.emptyList();
                } else {
                    result = parse( new Yaml().load( (String) data ), existingTasks );
                }
            } else if (data instanceof Map) {
                result = parseTasks( (Map<String, Map>)data, existingTasks );
            } 
            return result;
        }

        static List<TaskMetaData> parseTasks( Map<String, Map>tasks, List<? extends TaskMetaData> existingTasks) {
            List<TaskMetaData> result = new ArrayList<TaskMetaData>();

            for (String rubyClassName :  tasks.keySet()) {
                result.add( createOrUpdateTaskMetaData( rubyClassName, tasks.get( rubyClassName ), existingTasks ) );
            }
            
            return result;
        }

        @SuppressWarnings("rawtypes")
        static TaskMetaData createOrUpdateTaskMetaData(String rubyClassName, Map options, List<? extends TaskMetaData> existingTasks) {
            if (options == null) {
                options = Collections.EMPTY_MAP;
            }

            TaskMetaData task = null;

            for (TaskMetaData each : existingTasks) {
                if (rubyClassName.equals( each.getSimpleName() )) {
                    task = each; 
                    break;
                }
            }
            if (task == null) {
                task = new TaskMetaData();
                task.setRubyClassName( rubyClassName );
            }
            
            task.setConcurrency( (Integer)options.get( "concurrency") );

            return task;
        }

    }
}