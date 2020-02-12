import React from "react"
import { useObserver } from "mobx-react"
import { TableCell, TableRow, IconButton } from "@material-ui/core"
import { Delete } from "@material-ui/icons"

import { VariantsConfig, TaskType, ProjectType } from "../../stores/model"
import Select from "./Select"
import AddComponent from "./AddComponent"
import useStores from "../../hooks/useStores"
import PlatformRow from "./PlatformRow"


type VariantRowProps = {
    config: VariantsConfig
    treeID: string
    onDelete: () => void
    projectType: ProjectType
    type: TaskType
}

const VariantRow: React.FC<VariantRowProps> = props => {

    const { configStore } = useStores()

    return useObserver(() => {

        const { config, treeID, onDelete, projectType, type } = props
        const { taskVariantsMap} = configStore

        const onPlatformDelete = (id: string): void => {
            delete config.platforms![id]
        }

        const unselectedPlatforms = config.platforms && type === "BUILD" && configStore.platforms
            .filter(platform => !config.platforms![platform.id])

        const cell = (
            <div style={{ display: "flex", flexDirection: "row" }}>
                {Object.entries(config.map).map(([id, value]) => {
                    const taskVariant = taskVariantsMap[id]
                    return (
                        <Select
                            options={Object.values(taskVariant.variants).map(
                                variant => variant.id
                            )}
                            label={id}
                            value={value || taskVariant.defaultValue}
                            onChange={(value: string) => {
                                config.map[id] = value
                            }}
                            key={id}
                        />
                    )
                })}
                {
                    unselectedPlatforms && <AddComponent
                        label={`Add test platform`}
                        items={unselectedPlatforms}
                        onAdd={(taskId) => {
                            if (!config.platforms) {
                                config.platforms = {}
                            }
                            config.platforms[taskId] = { tasks: {} }
                        }} />
                }
            </div>
        )

        return (
            <React.Fragment>
                <TableRow>
                    <TableCell></TableCell>
                    {projectType === "JDK_PROJECT" &&
                        <TableCell></TableCell>}
                    <TableCell>{type === "BUILD" && cell}</TableCell>
                    <TableCell></TableCell>
                    <TableCell></TableCell>
                    <TableCell>{type === "TEST" && cell}</TableCell>
                    <TableCell>
                        <IconButton onClick={onDelete}>
                            <Delete />
                        </IconButton>
                    </TableCell>
                </TableRow>
                {type === "BUILD" && config.platforms && Object.entries(config.platforms)
                    .map(([id, config]) =>
                        <PlatformRow
                            config={config}
                            id={id}
                            key={treeID + id}
                            treeID={treeID + id}
                            onDelete={onPlatformDelete}
                            projectType={projectType}
                            type="TEST"
                        />)}
            </React.Fragment>
        )
    })
}

export default VariantRow
