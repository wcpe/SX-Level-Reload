package top.wcpe.sxlevel.state

/**
 * 由 WCPE 在 2024/2/23 14:34 创建
 * <p>
 * Created by WCPE on 2024/2/23 14:34
 * <p>
 * <p>
 * GitHub  : <a href="https://github.com/wcpe">wcpe 's GitHub</a>
 * <p>
 * QQ      : 1837019522
 * @author : WCPE
 * @since  : v1.6.0-SNAPSHOT
 */
sealed class MaxExpResultState {
    object MaxLevel : MaxExpResultState()
    data class NotLevelUpPermission(val level: Int) : MaxExpResultState()
    data class Success(val value: Int) : MaxExpResultState()
    object Fail : MaxExpResultState()
}